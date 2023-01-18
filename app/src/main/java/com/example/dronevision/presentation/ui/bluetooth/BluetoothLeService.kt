package com.example.dronevision.presentation.ui.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.util.Log
import java.io.FileDescriptor
import java.util.*


// Служба, которая взаимодействует с BLE-устройством через Android BLE API
class BluetoothLeService: Service() {
   // private val TAG = BluetoothLeService.getSimpleName()

    private lateinit var mBluetoothManager: BluetoothManager
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mBluetoothDeviceAddress: String
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService() : BluetoothLeService {
            return this@BluetoothLeService
        }
    }


    enum class State {
        STATE_DISCONNECTED,
        STATE_CONNECTING,
        STATE_CONNECTED
    }

    companion object {
        private var mConnectionState = State.STATE_DISCONNECTED
        private lateinit var mBluetoothGatt: BluetoothGatt

        val ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
        val ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
        val ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
        val ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
        val EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
        // Устанавливаем UUID, который используется для услуг измерения пульса
        var myUUID: UUID =
            UUID.fromString("00001112-0000-1000-8000-00805f9b34fb")
    }

    private var bluetoothAdapter: BluetoothAdapter? = null

    fun initialize(): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            return false
        }
        return true
    }

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice, context: Context){
        mBluetoothGatt = device.connectGatt(context, false, mGattCallback)
        getSupportedGattServices()
       val z = device.uuids
    }

    fun getSupportedGattServices(): List<BluetoothGattService?>? {
        mBluetoothGatt.services[0].getCharacteristic(mBluetoothGatt.services[0].uuid)
        return mBluetoothGatt?.services
    }


    // Различные методы обратного вызова, определённые в BLE API
    private val  mGattCallback: BluetoothGattCallback =
    object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            val intentAction: String
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED
                mConnectionState = State.STATE_CONNECTED
                broadcastUpdate(intentAction);
                Log.i("[Service]", "Connected to GATT server.");
                Log.i("[Service]", "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = State.STATE_DISCONNECTED;
                Log.i("[Service]", "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w("[Service]", "onServicesDiscovered received: $status");
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
       // sendBroadcast(intent)
    }

    private fun broadcastUpdate(
        action: String,
        characteristic: BluetoothGattCharacteristic
    ) {
        val intent = Intent(action)

        // Это специальная обработка для пульсометра
        // Извлечение данных осуществляется согласно спецификации профиля
        if (myUUID == characteristic.uuid) {
            val flag = characteristic.properties
            var format = -1
            if (flag and 0x01 != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16
                Log.d("[Service]", "Heart rate format UINT16.")
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8
                Log.d("[Service]", "Heart rate format UINT8.")
            }
            val heartRate = characteristic.getIntValue(format, 1)
            Log.d("[Service]", String.format("Received heart rate: %d", heartRate))
            intent.putExtra(EXTRA_DATA, heartRate.toString())
        } else {
            // For all other profiles, writes the data formatted in HEX.
            val data = characteristic.value
            if (data != null && data.size > 0) {
                val stringBuilder = StringBuilder(data.size)
                for (byteChar in data) stringBuilder.append(String.format("%02X ", byteChar))
                intent.putExtra(
                    EXTRA_DATA, """
     ${String(data)}
     $stringBuilder
     """.trimIndent()
                )
            }
        }
        sendBroadcast(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
}

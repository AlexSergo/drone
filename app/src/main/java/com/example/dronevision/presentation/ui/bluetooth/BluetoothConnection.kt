package com.example.dronevision.presentation.ui.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.MapActivityListener
import com.google.gson.Gson

class BluetoothConnection(
    private val adapter: BluetoothAdapter,
    private val context: Context,
    private val listener: MapActivityListener
) {

    lateinit var connectionThread: ConnectThread
    private var isConnected: Boolean = false

    fun getAdapter(): BluetoothAdapter {
        return adapter
    }

    @SuppressLint("MissingPermission")
    fun connect(mac: String){
        if (!adapter.isEnabled || mac.isEmpty())
            return
        val device = adapter.getRemoteDevice(mac)
       // adapter.bluetoothLeScanner.startScan(leScanCallback)
     //   val service = BluetoothLeService(context)
       /* service.connect(device)
            .done{
                Log.e("ZZZ", "CONNECTED!")
            }
            .fail { device, status ->
                Log.e("ZZZ", "FAIL!, $status")
            }
            .enqueue()*/
       // service.connect(device, context)
        device.let {
            connectionThread = ConnectThread(it, context, listener)
            connectionThread.start()
        }
        isConnected = true
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            result.device
        }
    }

    fun sendMessage(message: String){
        if (isConnected)
            connectionThread.receiveThread.sendMessage(message.toByteArray())
    }

    fun sendMessage(technic: Technic){
        if (isConnected)
            connectionThread.receiveThread.sendMessage((Gson().toJson(technic)).toByteArray())
    }
}
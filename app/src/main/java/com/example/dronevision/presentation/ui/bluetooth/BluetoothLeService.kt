package com.example.dronevision.presentation.ui.bluetooth

import android.bluetooth.*
import android.content.Context
import com.example.dronevision.utils.printGattTable
import no.nordicsemi.android.ble.BleManager


class BluetoothLeService(context: Context): BleManager(context){

    private lateinit var controlRequest: BluetoothGattCharacteristic
    private lateinit var controlResponse: BluetoothGattCharacteristic
    private lateinit var workTime: BluetoothGattCharacteristic

    override fun getGattCallback(): BleManagerGattCallback {
        return BleControlManagerGattCallback()
    }

     private class BleControlManagerGattCallback: BleManagerGattCallback(){
        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
           // val controlService = gatt.getService()
          //  val workTimeService = gatt.getService()
            gatt.printGattTable()
            return false
        }

        override fun onServicesInvalidated() {

        }

    }
}

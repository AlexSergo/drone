package com.example.dronevision.presentation.delegates

import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.dronevision.presentation.ui.bluetooth.BluetoothConnection
import com.example.dronevision.presentation.ui.bluetooth.BluetoothReceiver

class BluetoothHandlerImpl: BluetoothHandler {

    override fun setupBluetooth(context: Context, systemService: Any,
                                messageListener: BluetoothReceiver.MessageListener): BluetoothConnection {
        val bluetoothManager = systemService as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        return BluetoothConnection(
            bluetoothAdapter,
            context = context, listener = messageListener
        )
    }

}
package com.example.dronevision.presentation.delegates

import android.content.Context
import com.example.dronevision.presentation.ui.bluetooth.BluetoothConnection
import com.example.dronevision.presentation.ui.bluetooth.BluetoothReceiver

interface BluetoothHandler {
     fun setupBluetooth(context: Context, systemService: Any,
                                messageListener: BluetoothReceiver.MessageListener): BluetoothConnection
}
package com.example.dronevision.presentation.delegates

import android.content.Context
import com.example.dronevision.presentation.ui.MapActivityListener
import com.example.dronevision.presentation.ui.bluetooth.BluetoothConnection

interface BluetoothHandler {
     fun setupBluetooth(
         context: Context, systemService: Any,
     listener: MapActivityListener): BluetoothConnection
     fun sendMessage(message: String)
}
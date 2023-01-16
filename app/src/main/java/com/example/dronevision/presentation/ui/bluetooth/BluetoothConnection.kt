package com.example.dronevision.presentation.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.example.dronevision.presentation.ui.MapActivityListener

class BluetoothConnection(
    private val adapter: BluetoothAdapter,
    private val context: Context,
    private val listener: MapActivityListener
) {

    lateinit var connectionThread: ConnectThread

    fun getAdapter(): BluetoothAdapter {
        return adapter
    }

    fun connect(mac: String){
        if (!adapter.isEnabled || mac.isEmpty())
            return
        val device = adapter.getRemoteDevice(mac)
        device.let {
            connectionThread = ConnectThread(it, context, listener)
            connectionThread.start()
        }
    }

    fun sendMessage(message: String){

        connectionThread.receiveThread.sendMessage(message.toByteArray())
    }
}
package com.example.dronevision.presentation.ui.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.MapActivityListener
import com.google.gson.Gson


class BluetoothConnection(
    private val adapter: BluetoothAdapter,
    private val context: Context,
    private val listener: MapActivityListener
) {

    lateinit var connectionThread: ConnectThread
    private var _isConnected: Boolean = false
    val isConnected: Boolean get() = _isConnected

    fun getAdapter(): BluetoothAdapter {
        return adapter
    }

    @SuppressLint("MissingPermission")
    fun connect(mac: String){
        if (!adapter.isEnabled || mac.isEmpty())
            return
        val device = adapter.getRemoteDevice(mac)
        device.let {
            connectionThread = ConnectThread(it, context, listener)
            connectionThread.start()
        }
        _isConnected = true
    }


    fun sendMessage(message: String){
        if (_isConnected)
            connectionThread.receiveThread.sendMessage(message.toByteArray())
        else
            listener.showMessage("Невозможно отправить!")
    }

    fun sendMessage(technic: Technic){
        if (_isConnected)
            connectionThread.receiveThread.sendMessage((Gson().toJson(technic)).toByteArray())
        else
            listener.showMessage("Невозможно отправить!")
    }
}
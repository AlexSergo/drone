package com.example.dronevision.presentation.ui.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.dronevision.presentation.model.bluetooth.Message
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
class ConnectThread(private val device: BluetoothDevice,
                    private val context: Context,
                    private val listener: BluetoothReceiver.MessageListener): Thread() {

    val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    var socket: BluetoothSocket? = null
    lateinit var receiveThread: BluetoothReceiver

    companion object {
        private var _successConnectionLiveData = MutableLiveData<Boolean>()
        val successConnectionLiveData get() = _successConnectionLiveData
    }

    init {
        try {

                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))

        }catch (_: IOException){

        }
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        try {
            listener.onReceive(Message("Подключение...", true))
                socket?.connect()
                receiveThread = BluetoothReceiver(socket!!, listener)
                receiveThread.start()
                _successConnectionLiveData.postValue(true)
                listener.onReceive(Message("Подключено!", true))
        }catch (e: IOException){
            listener.onReceive(Message("Невозможно подключиться!", true))
            closeConnection()
        }
    }

    private fun closeConnection(){
        try {
            socket?.close()
        }catch (_: IOException){

        }
    }
}
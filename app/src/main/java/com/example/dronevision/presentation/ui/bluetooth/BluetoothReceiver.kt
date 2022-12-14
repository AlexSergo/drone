package com.example.dronevision.presentation.ui.bluetooth

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BluetoothReceiver(private val bluetoothSocket: BluetoothSocket,
                        private val listener: MessageListener): Thread() {

    private val BUFFER_SIZE = 256
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    init {
        try {
            inputStream = bluetoothSocket.inputStream
            outputStream = bluetoothSocket.outputStream
        }catch (_: IOException){

        }
    }

    override fun run() {
        val buffer = ByteArray(BUFFER_SIZE)
        while (true){
            try {
                val size = inputStream?.read(buffer)
                val message = String(buffer, 0, size!!)
                Log.d("MyLog", "Message $message")
                listener.onReceive(message)
            }catch (e: IOException){
                listener.onReceive("Ошибка, сбой соединения!")
                return
            }
        }
    }

    fun sendMessage(byteArray: ByteArray){
        try {
            outputStream?.write(byteArray)
        }catch (_: IOException){

        }
    }

    interface MessageListener{
        fun onReceive(message: String)
    }
}
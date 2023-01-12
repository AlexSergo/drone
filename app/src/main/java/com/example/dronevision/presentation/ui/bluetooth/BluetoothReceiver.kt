package com.example.dronevision.presentation.ui.bluetooth

import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.dronevision.presentation.model.bluetooth.Entities
import com.example.dronevision.presentation.model.bluetooth.Entity
import com.example.dronevision.presentation.model.bluetooth.Message
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class BluetoothReceiver(private val bluetoothSocket: BluetoothSocket,
                        private val listener: MessageListener): Thread() {

    private val BUFFER_SIZE = 512
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
        var message = ""
        while (true){
            try {
                val size = inputStream?.read(buffer)
                message += String(buffer, 0, size!!)
                val gson = GsonBuilder()
                    .setLenient()
                    .create()
                val start = message.indexOf("{\"Entities\"")
                val end = message.indexOf("]}") + 2
                val obj = message.substring(start, end)
                message = message.removePrefix(obj)
                    val entities = gson.fromJson(obj, Entities::class.java)
                    listener.onReceive(
                        Message("Данные получены!", false),
                        entities.entities.toMutableList()
                    )

            }catch (e: IOException){
                listener.onReceive(Message("Ошибка, сбой соединения!", true))
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
        fun onReceive(message: Message, entities: MutableList<Entity>? = null)
    }
}
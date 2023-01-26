package com.example.dronevision.presentation.ui.bluetooth

import android.bluetooth.BluetoothSocket
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.model.bluetooth.Entities
import com.example.dronevision.presentation.ui.MapActivityListener
import com.google.gson.GsonBuilder
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class BluetoothReceiver(
    private val bluetoothSocket: BluetoothSocket,
    private val listener: MapActivityListener
) : Thread() {
    
    private val BUFFER_SIZE = 512
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    
    init {
        try {
            inputStream = bluetoothSocket.inputStream
            outputStream = bluetoothSocket.outputStream
        } catch (_: IOException) {
        
        }
    }
    
    override fun run() {
        val buffer = ByteArray(BUFFER_SIZE)
        var message = ""
        while (true) {
            try {
                val size = inputStream?.read(buffer)
                message += String(buffer, 0, size!!)
                if (message.contains(MessageType.ID.name)) {
                    listener.receiveDeviceId(message.substring(4))
                    message = ""
                    continue
                }
                if (message.contains(MessageType.Entities.name))
                    message = parseDroneGson(message)
                if (message.contains("coordinates"))
                    message = parseTargetGson(message)
                
            } catch (e: IOException) {
                listener.showMessage("Ошибка, сбой соединения!")
                return
            }
        }
    }
    
    private fun parseTargetGson(message: String): String {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
        val target = gson.fromJson(message, Technic::class.java)
        listener.receiveTechnic(target)
        return ""
    }
    
    private fun parseDroneGson(message: String): String {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val start = message.indexOf("{\"Entities\"")
        val end = message.indexOf("]}") + 2
        val obj = message.substring(start, end)
        val resultMessage = message.removePrefix(obj)
        val entities = gson.fromJson(obj, Entities::class.java)
        listener.showDroneData(entities.entities.toMutableList())
        return resultMessage
    }
    
    fun sendMessage(byteArray: ByteArray) {
        try {
            outputStream?.write(byteArray)
        } catch (_: IOException) {
            listener.showMessage("Не удалось отправить!")
        }
    }
    
    enum class MessageType {
        ID,
        TechnicTypes,
        Entities
    }
}
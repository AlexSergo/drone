package com.example.dronevision.presentation.ui.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.example.dronevision.presentation.ui.MapActivityListener
import java.io.IOException
import java.lang.reflect.Method
import java.util.*


@SuppressLint("MissingPermission")
class ConnectThread(private val device: BluetoothDevice,
                    private val context: Context,
                    private val listener: MapActivityListener): Thread() {

    private val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    private var socket: BluetoothSocket? = null
    lateinit var receiveThread: BluetoothReceiver

    init {
        try {
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
          // socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid))
            //    socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(uuid))
        }catch (_: IOException){

        }
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        try {
            listener.showMessage("Подключение...")
                socket?.connect()
                receiveThread = BluetoothReceiver(socket!!, listener)
                receiveThread.start()
            listener.showMessage("Подключено!")
        }catch (e: IOException){
            listener.showMessage("Невозможно подключиться!")
            closeConnection()
        }
    }

    fun isConnected(device: BluetoothDevice): Boolean {
        return try {
            val m: Method = device.javaClass.getMethod("isConnected")
            m.invoke(device) as Boolean
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    private fun closeConnection(){
        try {
            socket?.close()
        }catch (_: IOException){

        }
    }
}
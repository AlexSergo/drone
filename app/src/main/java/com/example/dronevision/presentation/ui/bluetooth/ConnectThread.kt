package com.example.dronevision.presentation.ui.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.dronevision.presentation.model.Message
import java.io.IOException
import java.util.*

class ConnectThread(private val device: BluetoothDevice,
                    private val context: Context,
                    private val listener: BluetoothReceiver.MessageListener): Thread() {

    val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    var socket: BluetoothSocket? = null
    lateinit var receiveThread: BluetoothReceiver

    init {
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
            }
        }catch (_: IOException){

        }
    }

    override fun run() {
        try {
            listener.onReceive(Message("Подключение...", true))
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                socket?.connect()
                receiveThread = BluetoothReceiver(socket!!, listener)
                receiveThread.start()
                listener.onReceive(Message("Подключено!", true))

            }
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
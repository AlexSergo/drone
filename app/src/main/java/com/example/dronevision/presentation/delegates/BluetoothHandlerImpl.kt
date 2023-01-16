package com.example.dronevision.presentation.delegates

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.example.dronevision.presentation.ui.MapActivityListener
import com.example.dronevision.presentation.ui.bluetooth.BluetoothConnection
import com.example.dronevision.presentation.ui.bluetooth.BluetoothReceiver
import java.io.IOException
import java.util.*

class BluetoothHandlerImpl: BluetoothHandler {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothConnection: BluetoothConnection
    private lateinit var receiver: BluetoothReceiver
    private lateinit var listener: MapActivityListener
    private var socket: BluetoothSocket? = null
    val uuid = "00001101-0000-1000-8000-00805F9B34FB"

    override fun setupBluetooth(
        context: Context, systemService: Any, listener: MapActivityListener
    ): BluetoothConnection {
        val bluetoothManager = systemService as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        bluetoothConnection = BluetoothConnection(bluetoothAdapter, context, listener)
        this.listener = listener
        AcceptThread().start()
        return bluetoothConnection
    }

    override fun sendMessage(message: String){
        socket?.let {
            receiver.sendMessage(message.toByteArray())
        }
    }

    @SuppressLint("MissingPermission")
    private inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("test", UUID.fromString(uuid))
        }

        override fun run() {
            var shouldLoop = true
            while (shouldLoop) {
                socket = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    null
                }
                socket?.also {
                    receiver = BluetoothReceiver(it, listener)
                    receiver.start()
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
            // Keep listening until exception occurs or a socket is returned
        }

        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
            }
        }
    }

}
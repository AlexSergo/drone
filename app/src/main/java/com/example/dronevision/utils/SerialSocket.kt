package com.example.dronevision.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDeviceConnection
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.io.IOException

class SerialSocket(
    context: Context?,
    connection: UsbDeviceConnection?,
    serialPort: UsbSerialPort?
) :
    SerialInputOutputManager.Listener {
    private var connection: UsbDeviceConnection? = null
    private var context: Context? = null
    private var disconnectBroadcastReceiver: BroadcastReceiver? = null
    private var ioManager: SerialInputOutputManager? = null
    private var listener: SerialListener? = null
    private var serialPort: UsbSerialPort? = null

    init {
            this.context = context
            this.connection = connection
            this.serialPort = serialPort
            disconnectBroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context2: Context, intent: Intent) {
                    listener?.onSerialIoError(IOException("background disconnect"))
                    disconnect()
                }
            }
    }

    val name: String
        get() = serialPort!!.driver.javaClass.simpleName.replace("SerialDriver", "")

    @Throws(IOException::class)
    fun connect(listener: SerialListener?) {
        this.listener = listener
        context!!.registerReceiver(
            disconnectBroadcastReceiver,
            IntentFilter("ru.niissu.veterok.Disconnect")
        )
        val serialInputOutputManager = SerialInputOutputManager(serialPort, this)
        ioManager = serialInputOutputManager
        serialInputOutputManager.start()
    }

    fun disconnect() {
        listener = null
        val serialInputOutputManager = ioManager
        if (serialInputOutputManager != null) {
            serialInputOutputManager.listener = null as SerialInputOutputManager.Listener?
            ioManager!!.stop()
            ioManager = null
        }
        val usbSerialPort = serialPort
        if (usbSerialPort != null) {
            try {
                usbSerialPort.close()
            } catch (var6: Exception) {
            }
            serialPort = null
        }
        serialPort = null
        val usbDeviceConnection = connection
        if (usbDeviceConnection != null) {
            usbDeviceConnection.close()
            connection = null
        }
        try {
            context!!.unregisterReceiver(disconnectBroadcastReceiver)
        } catch (var5: Exception) {
        }
    }

    @Throws(IOException::class)
    fun write(data: ByteArray?) {
        val usbSerialPort = serialPort
        if (usbSerialPort == null) {
            throw IOException("not connected")
        } else {
            usbSerialPort.write(data, 100)
        }
    }

    override fun onNewData(data: ByteArray) {
        listener?.onSerialRead(data)
    }

    override fun onRunError(e: Exception) {
        listener?.onSerialIoError(e)
    }

    companion object {
        private const val WRITE_WAIT_MILLIS = 100
    }
}

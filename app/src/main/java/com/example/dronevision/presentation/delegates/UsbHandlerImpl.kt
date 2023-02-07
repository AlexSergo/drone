package com.example.dronevision.presentation.delegates

import android.content.Context
import android.hardware.usb.UsbManager
import android.widget.Toast
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.io.IOException

class UsbHandlerImpl: UsbHandler {
    private lateinit var context: Context
    private var usbManager: UsbManager? = null
    private var port: UsbSerialPort? = null

    override fun setupUsbConnection(context: Context, usbManager: UsbManager?){
        this.context = context
        this.usbManager = usbManager

        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
            Toast.makeText(context, "Нет соединения с Р-187-П1", Toast.LENGTH_SHORT).show()
            return
        }

        // Open a connection to the first available driver.
        val driver = availableDrivers[0]
        val connection = usbManager!!.openDevice(driver.device)
        if (connection != null){
            port = driver.ports[0] // Most devices have just one port (port 0)

            try {
                port?.let {
                    it.open(connection)
                    it.setParameters(
                        921600,
                        8,
                        UsbSerialPort.STOPBITS_1,
                        UsbSerialPort.PARITY_NONE
                    )
                    it.write("AT\$SERIALMODE=1\r\n".toByteArray(), 300)
                    it.write("request".toByteArray(), 300);
                }
            }catch (_: IOException){

            }
        }
    }

    fun sendMessage(issi: String, message: String){
        port?.write(RadioHandler.createRadioMessage(issi, message).toByteArray(), 300)
    }
}
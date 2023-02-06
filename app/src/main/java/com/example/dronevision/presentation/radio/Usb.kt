package com.example.dronevision.presentation.radio

import android.content.Context
import android.hardware.usb.UsbManager
import androidx.core.content.ContextCompat.getSystemService
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber


class Usb {
/*    fun init(){
        val manager = getSystemService<Any>(Context.USB_SERVICE) as UsbManager?
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            return
        }

        // Open a connection to the first available driver.
        val driver = availableDrivers[0]
        val connection = manager!!.openDevice(driver.device)
            ?: // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            return

        val port = driver.ports[0] // Most devices have just one port (port 0)

        port.open(connection)
        port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
    }*/
}
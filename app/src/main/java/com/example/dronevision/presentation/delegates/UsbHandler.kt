package com.example.dronevision.presentation.delegates

import android.content.Context
import android.hardware.usb.UsbManager

interface UsbHandler {
    fun setupUsbConnection(context: Context, usbManager: UsbManager?)
}
package com.example.dronevision.utils

interface SerialListener {
    fun onSerialConnect()
    fun onSerialConnectError(var1: Exception?)
    fun onSerialIoError(var1: Exception?)
    fun onSerialRead(var1: ByteArray?)
}

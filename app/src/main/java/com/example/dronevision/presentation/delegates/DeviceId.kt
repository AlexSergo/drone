package com.example.dronevision.presentation.delegates

import android.content.Context

interface DeviceId {
    fun getDeviceId(context: Context): String?
}
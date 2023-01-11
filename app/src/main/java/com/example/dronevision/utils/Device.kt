package com.example.dronevision.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

object Device {
    private lateinit var _id: String
    val id: String  get() =  _id

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String? {
        _id = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return _id
    }
}
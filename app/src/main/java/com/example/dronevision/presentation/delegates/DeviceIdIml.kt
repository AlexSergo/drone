package com.example.dronevision.presentation.delegates

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

class DeviceIdIml: DeviceId {
    @SuppressLint("HardwareIds")
    override fun getDeviceId(context: Context): String? {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}
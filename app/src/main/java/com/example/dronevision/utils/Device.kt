package com.example.dronevision.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.provider.Settings

object Device {
    private lateinit var _id: String
    val id: String  get() =  _id

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        _id = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return _id
    }

     fun setClipboard(context: Context, text: String) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }
}
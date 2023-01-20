package com.example.dronevision.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context) {
    private val PREFS_NAME = "my_prefs"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(KEY_NAME: String, value: String) {
        sharedPref.let {
            val editor: SharedPreferences.Editor = sharedPref.edit()

            editor.putString(KEY_NAME, value)

            editor.apply()
        }
    }

    fun getValue(KEY_NAME: String): String?{
        sharedPref.let {
            return sharedPref.getString(KEY_NAME, null)
        }
        return null
    }
}
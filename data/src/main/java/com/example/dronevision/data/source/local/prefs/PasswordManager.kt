package com.example.dronevision.data.source.local.prefs

import android.content.Context
import android.content.SharedPreferences
import com.example.dronevision.data.R

class PasswordManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    
    companion object {
        const val AUTH_PASSWORD = "my_password"
    }
    
    fun addPassword(fileName: String) {
        prefs.edit().putString(AUTH_PASSWORD, fileName).apply()
    }
    
    fun getPassword(): String? = prefs.getString(AUTH_PASSWORD, null)
    
    fun deletePassword() {
        prefs.edit().putString(AUTH_PASSWORD, null).apply()
    }
}
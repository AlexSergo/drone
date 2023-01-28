package com.example.dronevision.data.source.local.prefs

import android.content.Context
import android.content.SharedPreferences
import com.example.dronevision.data.R

class LoginManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    
    companion object {
        const val AUTH_LOGIN = "auth_login"
    }
    
    fun addLogin(login: String) {
        prefs.edit().putString(AUTH_LOGIN, login).apply()
    }
    
    fun getLogin(): String? = prefs.getString(AUTH_LOGIN, null)
    
    fun deleteLogin() {
        prefs.edit().putString(AUTH_LOGIN, null).apply()
    }
}
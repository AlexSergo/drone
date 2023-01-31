package com.example.dronevision.data.source.local.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.dronevision.data.R

class KeyManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val SECRET_KEY = "SECRET_KEY"

        private var _secretLiveData = MutableLiveData<String>()
        val secretLiveData get() = _secretLiveData
    }

    fun saveKey(key: String) {
        prefs.edit().putString(SECRET_KEY, key).apply()
        _secretLiveData.postValue(key)
    }

    fun getKey(): String? = prefs.getString(SECRET_KEY, null)

    fun deletePassword() {
        prefs.edit().putString(SECRET_KEY, null).apply()
    }
}
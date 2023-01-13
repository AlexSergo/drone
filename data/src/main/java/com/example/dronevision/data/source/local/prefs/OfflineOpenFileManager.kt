package com.example.dronevision.data.source.local.prefs

import android.content.Context
import android.content.SharedPreferences
import com.example.dronevision.data.R

class OfflineOpenFileManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    
    companion object {
        const val OFFLINE_MAP_FILE_NAME = "offlineMapFileName"
    }
    
    fun addFileName(fileName: String) {
        prefs.edit().putString(OFFLINE_MAP_FILE_NAME, fileName).apply()
    }
    
    fun getFileName(): String? = prefs.getString(OFFLINE_MAP_FILE_NAME, null)
    
    fun deleteFileName() {
        prefs.edit().putString(OFFLINE_MAP_FILE_NAME, null).apply()
    }
}
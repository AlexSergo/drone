package com.example.dronevision.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


object PermissionTools {
    private val REQUIRED_PERMISSION_LIST = arrayOf<String>(
        Manifest.permission.VIBRATE,  // Gimbal rotation
        Manifest.permission.INTERNET,  // API requests
        Manifest.permission.ACCESS_WIFI_STATE,  // WIFI connected products
        Manifest.permission.ACCESS_COARSE_LOCATION,  // Maps
        Manifest.permission.ACCESS_NETWORK_STATE,  // WIFI connected products
        Manifest.permission.ACCESS_FINE_LOCATION,  // Maps
        Manifest.permission.CHANGE_WIFI_STATE,  // Changing between WIFI and USB connection
        Manifest.permission.WRITE_EXTERNAL_STORAGE,  // Log files
        Manifest.permission.BLUETOOTH,  // Bluetooth connected products
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADMIN,  // Bluetooth connected products
        Manifest.permission.READ_EXTERNAL_STORAGE,  // Log files
    )
    
    private const val REQUEST_PERMISSION_CODE = 12345
    private val missingPermission: MutableList<String> = ArrayList()
    
    
    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    fun checkAndRequestPermissions(appCompatActivity: AppCompatActivity) {
        // Check for permissions
        for (permission in REQUIRED_PERMISSION_LIST) {
            if (ContextCompat.checkSelfPermission(
                    appCompatActivity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                missingPermission.add(permission)
            }
        }
        // Request for missing permissions
        if (!missingPermission.isEmpty()) {
            ActivityCompat.requestPermissions(
                appCompatActivity,
                missingPermission.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        }
    }
}
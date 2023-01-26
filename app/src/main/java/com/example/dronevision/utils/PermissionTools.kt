package com.example.dronevision.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker


object PermissionTools {
    private val REQUIRED_PERMISSION_LIST = arrayOf(
        Manifest.permission.INTERNET,  // API requests
        Manifest.permission.ACCESS_WIFI_STATE,  // WIFI connected products
        Manifest.permission.ACCESS_COARSE_LOCATION,  // Maps
        Manifest.permission.ACCESS_NETWORK_STATE,  // WIFI connected products
        Manifest.permission.ACCESS_FINE_LOCATION,  // Maps
        Manifest.permission.CHANGE_WIFI_STATE,  // Changing between WIFI and USB connection
        Manifest.permission.WRITE_EXTERNAL_STORAGE,  // Log files
        Manifest.permission.BLUETOOTH,  // Bluetooth connected products
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_PRIVILEGED,
        Manifest.permission.BLUETOOTH_ADMIN,  // Bluetooth connected products
        Manifest.permission.READ_EXTERNAL_STORAGE,  // Log files
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    )
    
    private const val REQUEST_PERMISSION_CODE = 12345
    private val missingPermission: MutableList<String> = ArrayList()
    
    fun checkStoragePermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION")
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(
                        String.format(
                            "package:%s",
                            *arrayOf<Any>(activity.applicationContext.applicationContext.packageName)
                        )
                    )
                    activity.startActivityForResult(intent, 2296)
                } catch (e: Exception) {
                    val intent2 = Intent()
                    intent2.action = "android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION"
                    activity.startActivityForResult(intent2, 2296)
                }
            }
        } else if (Build.VERSION.SDK_INT >= 23 && PermissionChecker.checkSelfPermission(
                activity.applicationContext,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                REQUEST_PERMISSION_CODE
            )
        }
    }
    
    fun checkLocationPermissions(activity: Activity): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // when permission is already grant
            true
        } else {
            // when permission is denied
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION_CODE
            )
            Toast.makeText(
                activity,
                "Необходимо дать приложению разрешение на геолокацию",
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }
    
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
        if (missingPermission.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                appCompatActivity,
                missingPermission.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        }
    }
}
package com.example.dronevision.presentation.delegates

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

class PermissionHandlerImpl : PermissionHandler {
    
    override fun checkStoragePermissions(activity: Activity) {
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
                1
            )
        }
    }
    
    override fun checkLocationPermissions(activity: Activity): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                activity,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // when permission is already grant
            true
        } else {
            // when permission is denied
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(ACCESS_FINE_LOCATION),
                100
            )
            Toast.makeText(
                activity,
                "Необходимо дать приложению разрешение на геолокацию",
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }
}
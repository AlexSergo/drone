package com.example.dronevision.presentation.delegates

import android.app.Activity
import android.content.Context

interface PermissionHandler {
    fun checkStoragePermissions(activity: Activity)
    fun checkLocationPermissions(activity: Activity): Boolean
}
package com.example.dronevision.presentation.delegates

import android.app.Activity

interface StoragePermissionHandler {
    fun checkStoragePermissions(activity: Activity)
}
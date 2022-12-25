package com.example.dronevision.presentation.delegates

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker

class StoragePermissionHandlerImpl: StoragePermissionHandler {

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
}
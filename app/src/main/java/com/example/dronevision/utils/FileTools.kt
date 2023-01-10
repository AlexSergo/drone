package com.example.dronevision.utils

import android.os.Environment
import java.io.File

object FileTools {
    fun createAppFolder() {
        val rootDirName = Environment.getExternalStorageDirectory().path
        val dirName = "$rootDirName/Drone Vision/"
        val folder = File(dirName)
        
        if (!folder.exists()) {
            val newFile = File(rootDirName, "Drone Vision")
            newFile.mkdir()
        }
    }
}
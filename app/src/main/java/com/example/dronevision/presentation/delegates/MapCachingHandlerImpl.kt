package com.example.dronevision.presentation.delegates

import android.content.Context
import android.widget.Toast
import org.osmdroid.tileprovider.cachemanager.CacheManager
import org.osmdroid.views.MapView

class MapCachingHandlerImpl : MapCachingHandler {
    override fun cacheMap(mapView: MapView, context: Context) {
        if (mapView.useDataConnection()) {
            val cacheManager = CacheManager(mapView)
            cacheManager.verifyCancel = true
            cacheManager.downloadAreaAsync(context, mapView.boundingBox, 1, 18)
        } else {
            Toast.makeText(
                context,
                "Скачивание невозможно в оффлайн-режиме",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
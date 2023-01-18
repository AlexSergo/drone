package com.example.dronevision.presentation.delegates

import android.content.Context
import org.osmdroid.views.MapView

interface MapCachingHandler {
    fun cacheMap(mapView: MapView, context: Context)
}
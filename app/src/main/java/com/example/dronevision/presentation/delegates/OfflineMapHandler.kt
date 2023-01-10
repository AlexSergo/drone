package com.example.dronevision.presentation.delegates

import android.content.Context
import com.example.dronevision.databinding.FragmentOsmdroidBinding
import org.osmdroid.views.MapView

interface OfflineMapHandler {
    fun offlineMode(mapView: MapView, context: Context)
}
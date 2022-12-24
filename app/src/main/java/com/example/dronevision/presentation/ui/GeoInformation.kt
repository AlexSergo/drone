package com.example.dronevision.presentation.ui

import com.example.dronevision.databinding.FragmentYandexMapBinding
import com.yandex.mapkit.geometry.Point

interface GeoInformation {
    fun showGeoInformation(
        binding: FragmentYandexMapBinding,
        cameraLat: Double, cameraLon: Double,
        azimuthPoint: Point
    )
}
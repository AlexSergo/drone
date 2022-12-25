package com.example.dronevision.presentation.delegates

import com.example.dronevision.databinding.FragmentOsmdroidBinding
import com.example.dronevision.databinding.FragmentYandexMapBinding
import com.yandex.mapkit.geometry.Point
import org.osmdroid.util.GeoPoint

interface GeoInformation {
    fun showGeoInformation(
        binding: FragmentYandexMapBinding,
        cameraTarget: Point,
        azimuthPoint: Point
    )
    fun showGeoInformation(binding: FragmentOsmdroidBinding,
                           cameraTarget: GeoPoint,
                                    azimuthPoint: GeoPoint)
}
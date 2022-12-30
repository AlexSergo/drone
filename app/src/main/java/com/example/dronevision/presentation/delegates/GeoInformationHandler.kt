package com.example.dronevision.presentation.delegates

import com.example.dronevision.databinding.FragmentOsmdroidBinding
import org.osmdroid.util.GeoPoint

interface GeoInformationHandler {
    fun showGeoInformation(
        binding: FragmentOsmdroidBinding,
        cameraTarget: GeoPoint,
        azimuthPoint: GeoPoint
    )
    fun getDistance(from: GeoPoint, to: GeoPoint): Double
}
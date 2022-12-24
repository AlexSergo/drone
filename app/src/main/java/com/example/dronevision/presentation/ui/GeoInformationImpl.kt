package com.example.dronevision.presentation.ui

import com.example.dronevision.databinding.FragmentYandexMapBinding
import com.example.dronevision.utils.MapTools
import com.example.dronevision.utils.NGeoCalc
import com.yandex.mapkit.geometry.Point

class GeoInformationImpl: GeoInformation {
    private var cameraLat: Double = 0.0
    private var cameraLon: Double = 0.0
    private lateinit var binding: FragmentYandexMapBinding
    override fun showGeoInformation(binding: FragmentYandexMapBinding,
                                    cameraLat: Double, cameraLon: Double,
                                    azimuthPoint: Point) {
        this.cameraLat = cameraLat
        this.cameraLon = cameraLon
        this.binding = binding
        showWgs82OnCard()
        calculateAzimuth(point = azimuthPoint)
        showSk42OnCard()
    }

    private fun showWgs82OnCard() {
        val latitudeText = String.format("%.6f", cameraLat)
        val longitudeText = String.format("%.6f", cameraLon)
        binding.latitude.text = "Широта = $latitudeText"
        binding.longitude.text = "Долгота = $longitudeText"
    }

    private fun showSk42OnCard() {
        val x = doubleArrayOf(0.0)
        val y = doubleArrayOf(0.0)

        NGeoCalc().wgs84ToPlane(
            x, y,
            doubleArrayOf(0.0),
            NGeoCalc.degreesToRadians(cameraLat),
            NGeoCalc.degreesToRadians(cameraLon),
            0.0
        )
        binding.plane.text = String.format(
            "X= %d  Y= %08d", *arrayOf<Any>(
                Integer.valueOf(x[0].toInt()), Integer.valueOf(y[0].toInt())
            )
        )
    }

    private fun calculateAzimuth(point: Point) {
        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        val azimuth = MapTools.angleBetween(point, cameraPositionTarget)
        val azimuthText = String.format("%.6f", azimuth)
        binding.azimuth.text = "Азимут = $azimuthText"
    }

}
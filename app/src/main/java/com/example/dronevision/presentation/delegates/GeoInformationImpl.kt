package com.example.dronevision.presentation.delegates

import com.example.dronevision.databinding.FragmentOsmdroidBinding
import com.example.dronevision.databinding.FragmentYandexMapBinding
import com.example.dronevision.utils.MapTools
import com.example.dronevision.utils.NGeoCalc
import com.yandex.mapkit.geometry.Point
import org.osmdroid.util.GeoPoint

class GeoInformationImpl: GeoInformation {
    private var cameraLat: Double = 0.0
    private var cameraLon: Double = 0.0

    override fun showGeoInformation(binding: FragmentYandexMapBinding,
                                    cameraTarget: Point,
                                    azimuthPoint: Point) {
        this.cameraLat = cameraTarget.latitude
        this.cameraLon = cameraTarget.longitude
        val lat_lon = showWgs82OnCard()
        val azimuth = calculateAzimuth(azimuthPoint.latitude, azimuthPoint.longitude)
        val plane = showSk42OnCard()

        binding.latitude.text = "Широта = " + lat_lon[0]
        binding.longitude.text = "Долгота = " + lat_lon[1]
        binding.plane.text = plane
        binding.azimuth.text = "Азимут = $azimuth"
    }

    override fun showGeoInformation(binding: FragmentOsmdroidBinding,
                                    cameraTarget: GeoPoint,
                                    azimuthPoint: GeoPoint) {
        this.cameraLat = cameraTarget.latitude
        this.cameraLon = cameraTarget.longitude
        val lat_lon = showWgs82OnCard().split(" ")
        val azimuth = calculateAzimuth(azimuthPoint.latitude, azimuthPoint.longitude)
        val plane = showSk42OnCard()

        binding.latitude.text = "Широта = " + lat_lon[0]
        binding.longitude.text = "Долгота = " + lat_lon[1]
        binding.plane.text = plane
        binding.azimuth.text = "Азимут = $azimuth"
    }

    private fun showWgs82OnCard(): String {
        val latitudeText = String.format("%.6f", cameraLat)
        val longitudeText = String.format("%.6f", cameraLon)
        return "$latitudeText $longitudeText"
    }

    private fun showSk42OnCard(): String {
        val x = doubleArrayOf(0.0)
        val y = doubleArrayOf(0.0)

        NGeoCalc().wgs84ToPlane(
            x, y,
            doubleArrayOf(0.0),
            NGeoCalc.degreesToRadians(cameraLat),
            NGeoCalc.degreesToRadians(cameraLon),
            0.0
        )
        return String.format(
            "X= %d  Y= %08d", *arrayOf<Any>(
                Integer.valueOf(x[0].toInt()), Integer.valueOf(y[0].toInt())
            )
        )
    }

    private fun calculateAzimuth(pointLat: Double, pointLon: Double): String {
        val azimuth = MapTools.angleBetween(Point(pointLat, pointLon), Point(cameraLat, cameraLon))
        val azimuthText = String.format("%.6f", azimuth)
        return azimuthText
    }
}
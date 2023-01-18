package com.example.dronevision.presentation.ui.find_location

import org.osmdroid.util.GeoPoint

interface FindGeoPointCallback {
    fun findGeoPoint(geoPoint: GeoPoint)
}
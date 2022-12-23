package com.example.dronevision.presentation.ui

import com.example.dronevision.presentation.ui.bluetooth.Entity
import com.yandex.mapkit.map.MapType

interface IMap{
    fun showLocationFromDrone(entities: List<Entity>)
    fun showLocationDialog()
    fun deleteAll()
    fun offlineMode()
}
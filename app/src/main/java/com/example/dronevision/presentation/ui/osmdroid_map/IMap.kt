package com.example.dronevision.presentation.ui.osmdroid_map

import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.model.bluetooth.Entity

interface IMap {
    fun showDataFromDrone(entities: List<Entity>)
    fun showLocationDialog()
    fun deleteAll()
    fun offlineMode()
    fun changeGridState(isGrid: Boolean)
    fun initTechnic()
    fun spawnTechnic(type: TechnicTypes, coords: Coordinates? = null)
    fun removeAim()
    fun initDroneMarker()
    fun setMapType(mapType: Int)
    fun cacheMap()
}
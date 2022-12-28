package com.example.dronevision.presentation.ui

import androidx.annotation.DrawableRes
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.ui.bluetooth.Entity
import com.example.dronevision.presentation.ui.targ.TargFragment
import com.yandex.mapkit.map.MapType
import org.osmdroid.views.overlay.Marker

interface IMap: TargFragment.TargetFragmentCallback{
    fun showDataFromDrone(entities: List<Entity>)
    fun showLocationDialog()
    fun deleteAll()
    fun offlineMode()
    fun changeGridState(isShow: Boolean)
    fun initTechnic()
    fun spawnTechnic(type: TechnicTypes,
                     coords: Coordinates? = null)
    fun removeAim()
    fun initDroneMarker()
}
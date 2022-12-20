package com.example.dronevision.presentation.ui

import com.example.dronevision.presentation.ui.bluetooth.Entity

interface IMap{
    fun showLocationFromDrone(entities: List<Entity>)
    fun showLocationDialog()
    fun deleteAll()
}
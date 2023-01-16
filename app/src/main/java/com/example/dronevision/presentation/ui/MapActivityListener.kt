package com.example.dronevision.presentation.ui

import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.model.bluetooth.Entity

interface MapActivityListener {
    fun showMessage(message: String)
    fun showDroneData(entities: MutableList<Entity>)
    fun receiveDeviceId(id: String)
    fun receiveTechnic(technic: Technic)
}

package com.example.dronevision.presentation.delegates

import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.osmdroid_map.IMap

interface RemoteDatabaseHandler{
    fun onDatabaseChangeListener(id: String, map: RemoteDatabaseCallback)
    fun sendMessage(destinationId: String, technic: Technic)
}
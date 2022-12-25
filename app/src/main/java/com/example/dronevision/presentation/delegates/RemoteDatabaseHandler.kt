package com.example.dronevision.presentation.delegates

import com.example.dronevision.presentation.ui.IMap
import com.google.firebase.database.DatabaseReference

interface RemoteDatabaseHandler{
    fun onDatabaseChangeListener(dRef: DatabaseReference, map: IMap)
}
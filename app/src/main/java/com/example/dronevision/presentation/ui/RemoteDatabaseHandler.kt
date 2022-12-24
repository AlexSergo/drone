package com.example.dronevision.presentation.ui

import com.google.firebase.database.DatabaseReference

interface RemoteDatabaseHandler{
    fun onDatabaseChangeListener(dRef: DatabaseReference, map: IMap)
}
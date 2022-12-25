package com.example.dronevision.presentation.delegates

import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.ui.IMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class RemoteDatabaseHandlerImpl: RemoteDatabaseHandler {

    override fun onDatabaseChangeListener(
        dRef: DatabaseReference, map : IMap
    ) {
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (null == snapshot.value) return
                val str = snapshot.value.toString().split(" ")

                map.spawnTechnic(
                    TechnicTypes.valueOf(str[0]),
                    Coordinates(x = str[1].toDouble(), y = str[2].toDouble())
                )
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
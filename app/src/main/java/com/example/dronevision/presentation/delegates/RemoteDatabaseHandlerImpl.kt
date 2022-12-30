package com.example.dronevision.presentation.delegates

import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.osmdroid_map.IMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RemoteDatabaseHandlerImpl: RemoteDatabaseHandler {

    private var databaseRef: DatabaseReference? = null

    override fun onDatabaseChangeListener(map : IMap) {
        checkNull()
        databaseRef?.addValueEventListener(object : ValueEventListener {
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

    override fun sendMessage(technic: Technic) {
        checkNull()
        val sb = StringBuilder()
        sb.append(technic.type.name)
        sb.append(" ")
        sb.append(technic.coords.x)
        sb.append(" ")
        sb.append(technic.coords.y)
        databaseRef?.setValue(sb.toString())
    }

    private fun checkNull(){
        if (databaseRef == null) {
            val database =
                Firebase.database("https://drone-6c66c-default-rtdb.asia-southeast1.firebasedatabase.app")
            databaseRef = database.getReference("message")
        }
    }
}
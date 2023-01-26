package com.example.dronevision.presentation.delegates

import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.osmdroid_map.IMap
import com.example.dronevision.utils.Device
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

interface RemoteDatabaseCallback{
    fun returnMessage(str: List<String>)
}


class RemoteDatabaseHandlerImpl: RemoteDatabaseHandler {

    private var databaseRef: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database =
        Firebase.database("https://drone-6c66c-default-rtdb.asia-southeast1.firebasedatabase.app")

    override fun onDatabaseChangeListener(id: String, callback: RemoteDatabaseCallback) {
        checkNull()
        databaseRef?.child(id)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (null == snapshot.value) return
                val str = snapshot.value.toString().split(" ")

                callback.returnMessage(str)


                databaseRef?.child(id)?.removeValue()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun sendMessage(destinationId: String, technic: Technic) {
        checkNull()
        if (firebaseAuth.currentUser?.email != Device.id + "@mail.ru")
            anonymousAuth()
        val dataMap = mutableMapOf<String, Any>()
        val sb = StringBuilder()
        sb.append(technic.division)
        sb.append(" ")
        sb.append(technic.technicTypes.name)
        sb.append(" ")
        sb.append(technic.coordinates.x)
        sb.append(" ")
        sb.append(technic.coordinates.y)
        dataMap["info"] = sb.toString()
        databaseRef?.child(destinationId)?.updateChildren(dataMap)
    }

    private fun anonymousAuth() {
        firebaseAuth.signInWithEmailAndPassword(Device.id + "@mail.ru", Device.id)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    val uid = firebaseAuth.currentUser?.uid.toString()
                    val dataMap = mutableMapOf<String, Any>()
                    dataMap["id"] = uid
                    dataMap["login"] = Device.id

                    database.reference.child("users").child(uid).updateChildren(dataMap)
                }

            }
    }

    private fun checkNull(){
        if (databaseRef == null)
            databaseRef = database.getReference("messages")

    }
}
package com.example.dronevision.data.local

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dronevision.data.local.model.TechnicEntity

@androidx.room.Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTechnic(technic: TechnicEntity)

    @Query("SELECT * FROM technics")
    suspend fun getTechnics(): List<TechnicEntity>

    @Delete
    suspend fun deleteTechnic(technic: TechnicEntity)

    @Query("DELETE FROM technics")
    suspend fun  deleteAll()
}
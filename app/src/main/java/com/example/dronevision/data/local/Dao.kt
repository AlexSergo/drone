package com.example.dronevision.data.local

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dronevision.data.model.TechnicEntity

@androidx.room.Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTechnic(technic: TechnicEntity)

    @Query("SELECT * FROM technics")
    suspend fun getTechnics(): List<TechnicEntity>
}
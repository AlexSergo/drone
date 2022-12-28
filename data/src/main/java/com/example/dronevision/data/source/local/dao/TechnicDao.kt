package com.example.dronevision.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dronevision.data.source.local.model.TechnicEntity

@Dao
interface TechnicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTechnic(technic: TechnicEntity)

    @Query("SELECT * FROM technics")
    suspend fun getTechnics(): List<TechnicEntity>

    @Delete
    suspend fun deleteTechnic(technic: TechnicEntity)

    @Query("DELETE FROM technics")
    suspend fun  deleteAllTechnics()
}
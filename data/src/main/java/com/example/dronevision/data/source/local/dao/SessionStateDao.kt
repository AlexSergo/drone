package com.example.dronevision.data.source.local.dao

import androidx.room.*
import com.example.dronevision.data.source.local.model.SessionStateEntity

@Dao
interface SessionStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSessionState(sessionStateEntity: SessionStateEntity)
    
    @Query("SELECT * FROM state_session LIMIT 1")
    suspend fun getSessionState(): SessionStateEntity
}
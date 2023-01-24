package com.example.dronevision.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dronevision.data.source.local.model.SubscriberEntity

@Dao
interface SubscribersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSubscriber(subscriberEntity: SubscriberEntity)

    @Query("SELECT * FROM subscribers")
    suspend fun getSubscribers(): List<SubscriberEntity>

    @Query("DELETE FROM subscribers WHERE name = :name")
    fun removeSubscriber(name: String)
}
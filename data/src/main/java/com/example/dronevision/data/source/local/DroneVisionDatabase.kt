package com.example.dronevision.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dronevision.data.source.local.dao.SessionStateDao
import com.example.dronevision.data.source.local.dao.SubscribersDao
import com.example.dronevision.data.source.local.dao.TechnicDao
import com.example.dronevision.data.source.local.model.SessionStateEntity
import com.example.dronevision.data.source.local.model.SubscriberEntity
import com.example.dronevision.data.source.local.model.TechnicEntity

@androidx.room.Database(entities = [TechnicEntity::class, SessionStateEntity::class, SubscriberEntity::class], version = 7, exportSchema = false)
abstract class DroneVisionDatabase : RoomDatabase() {
    
    abstract fun technicsDao(): TechnicDao
    abstract fun SessionStateDao(): SessionStateDao
    abstract fun SubscribersDao(): SubscribersDao
    
    companion object {
        @Volatile
        private var INSTANCE: DroneVisionDatabase? = null
        private const val DATABASE_NAME = "drone_vision_database"
        
        fun getInstance(context: Context): DroneVisionDatabase {
            var tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DroneVisionDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
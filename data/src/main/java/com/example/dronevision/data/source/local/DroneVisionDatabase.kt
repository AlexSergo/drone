package com.example.dronevision.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dronevision.data.source.local.model.TechnicEntity

@androidx.room.Database(entities = [TechnicEntity::class], version = 1, exportSchema = false)
abstract class DroneVisionDatabase : RoomDatabase() {
    
    abstract fun technicsDao(): TechnicDao
    
    companion object {
        @Volatile
        private var INSTANCE: DroneVisionDatabase? = null
        private const val DATABASE_NAME = "life_calendar_database"
        
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
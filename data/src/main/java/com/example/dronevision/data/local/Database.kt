package com.example.dronevision.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dronevision.data.local.model.TechnicEntity

@androidx.room.Database(entities = [TechnicEntity::class], version = 1, exportSchema = false)
abstract class Database: RoomDatabase() {
    abstract fun dao(): Dao

    companion object{
        @Volatile
        var database: Database? = null

        fun getInstance(context: Context): Database?{
            if (database == null){
                synchronized(this){
                    val db = Room.databaseBuilder(
                        context.applicationContext,
                        Database::class.java, "dronevision_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    database = db
                    return db
                }
            }
            return database
        }
    }
}
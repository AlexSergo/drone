package com.example.lifecalendar.di

import android.content.Context
import com.example.dronevision.data.source.local.DroneVisionDatabase
import com.example.dronevision.data.source.local.TechnicDao
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {
    
    @Provides
    fun provideDao(context: Context): TechnicDao =
        DroneVisionDatabase.getInstance(context).technicsDao()
}
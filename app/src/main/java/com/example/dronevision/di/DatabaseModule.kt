package com.example.dronevision.di

import android.content.Context
import com.example.dronevision.data.source.local.DroneVisionDatabase
import com.example.dronevision.data.source.local.dao.SessionStateDao
import com.example.dronevision.data.source.local.dao.SubscribersDao
import com.example.dronevision.data.source.local.dao.TechnicDao
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {
    
    @Provides
    fun provideTechnicDao(context: Context): TechnicDao =
        DroneVisionDatabase.getInstance(context).technicsDao()
    
    @Provides
    fun provideSessionStateDao(context: Context): SessionStateDao =
        DroneVisionDatabase.getInstance(context).SessionStateDao()

    @Provides
    fun provideSubscribersDao(context: Context): SubscribersDao =
        DroneVisionDatabase.getInstance(context).SubscribersDao()
}
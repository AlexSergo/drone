package com.example.lifecalendar.di

import com.example.dronevision.data.repository.TechnicRepositoryImpl
import com.example.dronevision.data.source.LocalDataSource
import com.example.dronevision.data.source.local.TechnicDao
import com.example.dronevision.domain.repository.TechnicRepository
import dagger.Module
import dagger.Provides

@Module(includes = [DatabaseModule::class])
class DataModule {
    
    @Provides
    fun providesLocalDataSource(technicDao: TechnicDao): LocalDataSource =
        LocalDataSource(technicDao = technicDao)
    
    @Provides
    fun provideTechnicRepository(localDataSource: LocalDataSource): TechnicRepository =
        TechnicRepositoryImpl(localDataSource = localDataSource)
}
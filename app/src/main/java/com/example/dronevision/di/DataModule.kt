package com.example.dronevision.di

import com.example.dronevision.data.repository.SessionStateRepositoryImpl
import com.example.dronevision.data.repository.TechnicRepositoryImpl
import com.example.dronevision.data.source.LocalDataSource
import com.example.dronevision.data.source.local.dao.SessionStateDao
import com.example.dronevision.data.source.local.dao.TechnicDao
import com.example.dronevision.domain.repository.SessionStateRepository
import com.example.dronevision.domain.repository.TechnicRepository
import dagger.Module
import dagger.Provides

@Module(includes = [DatabaseModule::class])
class DataModule {
    
    @Provides
    fun providesLocalDataSource(
        technicDao: TechnicDao,
        sessionStateDao: SessionStateDao
    ): LocalDataSource =
        LocalDataSource(technicDao = technicDao, sessionStateDao = sessionStateDao)
    
    @Provides
    fun provideTechnicRepository(localDataSource: LocalDataSource): TechnicRepository =
        TechnicRepositoryImpl(localDataSource = localDataSource)
    
    @Provides
    fun provideSessionStateRepository(localDataSource: LocalDataSource): SessionStateRepository =
        SessionStateRepositoryImpl(localDataSource = localDataSource)
}
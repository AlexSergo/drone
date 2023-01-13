package com.example.dronevision.di

import android.content.Context
import com.example.dronevision.data.repository.DroneVisionServiceRepositoryImpl
import com.example.dronevision.data.repository.SessionStateRepositoryImpl
import com.example.dronevision.data.repository.SubscribersRepositoryImpl
import com.example.dronevision.data.repository.TechnicRepositoryImpl
import com.example.dronevision.data.source.LocalDataSource
import com.example.dronevision.data.source.RemoteDataSource
import com.example.dronevision.data.source.local.dao.SessionStateDao
import com.example.dronevision.data.source.local.dao.SubscribersDao
import com.example.dronevision.data.source.local.dao.TechnicDao
import com.example.dronevision.data.source.local.prefs.OfflineOpenFileManager
import com.example.dronevision.data.source.remote.DroneVisionService
import com.example.dronevision.domain.repository.DroneVisionServiceRepository
import com.example.dronevision.domain.repository.SessionStateRepository
import com.example.dronevision.domain.repository.SubscribersRepository
import com.example.dronevision.domain.repository.TechnicRepository
import dagger.Module
import dagger.Provides

@Module(includes = [DatabaseModule::class, NetworkModule::class])
class DataModule {
    
    @Provides
    fun providesLocalDataSource(
        technicDao: TechnicDao,
        sessionStateDao: SessionStateDao,
        subscribersDao: SubscribersDao
    ): LocalDataSource =
        LocalDataSource(technicDao = technicDao, sessionStateDao = sessionStateDao, subscribersDao = subscribersDao)
    
    @Provides
    fun provideRemoteDataSource(droneVisionService: DroneVisionService): RemoteDataSource =
        RemoteDataSource(api = droneVisionService)
    
    @Provides
    fun provideTechnicRepository(localDataSource: LocalDataSource): TechnicRepository =
        TechnicRepositoryImpl(localDataSource = localDataSource)
    
    @Provides
    fun provideSessionStateRepository(localDataSource: LocalDataSource): SessionStateRepository =
        SessionStateRepositoryImpl(localDataSource = localDataSource)

    @Provides
    fun provideDroneVisionServiceRepository(remoteDataSource: RemoteDataSource): DroneVisionServiceRepository =
        DroneVisionServiceRepositoryImpl(remoteDataSource = remoteDataSource)

    @Provides
    fun provideSubscribersRepository(localDataSource: LocalDataSource): SubscribersRepository =
        SubscribersRepositoryImpl(localDataSource = localDataSource)
    
    @Provides
    fun provideOfflineOpenFileManager(context: Context): OfflineOpenFileManager =
        OfflineOpenFileManager(context)
}
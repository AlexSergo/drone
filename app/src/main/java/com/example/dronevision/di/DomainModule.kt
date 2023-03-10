package com.example.dronevision.di

import com.example.dronevision.data.source.SocketDataSourceImpl
import com.example.dronevision.domain.repository.*
import com.example.dronevision.domain.use_cases.*
import dagger.Module
import dagger.Provides
import java.net.Socket

@Module
class DomainModule {
    
    @Provides
    fun provideDeleteAllUseCase(technicRepository: TechnicRepository): DeleteAllUseCase =
        DeleteAllUseCase(technicRepository = technicRepository)
    
    @Provides
    fun provideDeleteTechnicUseCase(technicRepository: TechnicRepository): DeleteTechnicUseCase =
        DeleteTechnicUseCase(technicRepository = technicRepository)
    
    @Provides
    fun provideGetTechnicsUseCase(technicRepository: TechnicRepository): GetTechnicsUseCase =
        GetTechnicsUseCase(technicRepository = technicRepository)
    
    @Provides
    fun provideSaveTechnicUseCase(technicRepository: TechnicRepository): SaveTechnicUseCase =
        SaveTechnicUseCase(technicRepository = technicRepository)
    
    @Provides
    fun provideGetSessionStateUseCase(sessionStateRepository: SessionStateRepository): GetSessionStateUseCase =
        GetSessionStateUseCase(sessionStateRepository = sessionStateRepository)
    
    @Provides
    fun provideSaveSessionStateUseCase(sessionStateRepository: SessionStateRepository): SaveSessionStateUseCase =
        SaveSessionStateUseCase(sessionStateRepository = sessionStateRepository)

    @Provides
    fun provideGetIdUseCase(droneVisionServiceRepository: DroneVisionServiceRepository): GetIdUseCase =
        GetIdUseCase(repository = droneVisionServiceRepository)

    @Provides
    fun provideGetSubscribersUseCase(subscribersRepository: SubscribersRepository): GetSubscribersUseCase =
        GetSubscribersUseCase(repository = subscribersRepository)

    @Provides
    fun provideSaveSubscriberUseCase(subscribersRepository: SubscribersRepository): SaveSubscriberUseCase =
        SaveSubscriberUseCase(repository = subscribersRepository)

    @Provides
    fun provideRemoveSubscriberUseCase(subscribersRepository: SubscribersRepository): RemoveSubscriberUseCase =
        RemoveSubscriberUseCase(repository = subscribersRepository)

    @Provides
    fun provideSocketUseCase(dataSource: SocketDataSource): SocketUseCase {
        return SocketUseCase(dataSource)
    }
}
package com.example.dronevision.di

import com.example.dronevision.domain.repository.DroneVisionServiceRepository
import com.example.dronevision.domain.repository.SessionStateRepository
import com.example.dronevision.domain.repository.TechnicRepository
import com.example.dronevision.domain.use_cases.*
import dagger.Module
import dagger.Provides

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
}
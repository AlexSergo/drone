package com.example.lifecalendar.di

import com.example.dronevision.domain.repository.TechnicRepository
import com.example.dronevision.domain.use_cases.DeleteAllUseCase
import com.example.dronevision.domain.use_cases.DeleteTechnicUseCase
import com.example.dronevision.domain.use_cases.GetTechnicsUseCase
import com.example.dronevision.domain.use_cases.SaveTechnicUseCase
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
}
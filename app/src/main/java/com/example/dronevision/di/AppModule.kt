package com.example.dronevision.di

import android.content.Context
import com.example.dronevision.domain.use_cases.DeleteAllUseCase
import com.example.dronevision.domain.use_cases.DeleteTechnicUseCase
import com.example.dronevision.domain.use_cases.GetTechnicsUseCase
import com.example.dronevision.domain.use_cases.SaveTechnicUseCase
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidViewModelFactory
import com.example.dronevision.presentation.ui.yandex_map.YandexMapViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class AppModule(val context: Context) {
    
    @Provides
    fun provideContext(): Context = context
    
    @Provides
    fun provideOsmdroidViewModelFactory(
        getTechnicsUseCase: GetTechnicsUseCase,
        saveTechnicUseCase: SaveTechnicUseCase,
        deleteAllUseCase: DeleteAllUseCase,
        deleteTechnicUseCase: DeleteTechnicUseCase,
    ): OsmdroidViewModelFactory = OsmdroidViewModelFactory(
        getTechnicsUseCase = getTechnicsUseCase,
        saveTechnicUseCase = saveTechnicUseCase,
        deleteAllUseCase = deleteAllUseCase,
        deleteTechnicUseCase = deleteTechnicUseCase
    )
    
    @Provides
    fun provideYandexMapViewModelFactory(
        getTechnicsUseCase: GetTechnicsUseCase,
        saveTechnicUseCase: SaveTechnicUseCase,
        deleteAllUseCase: DeleteAllUseCase,
        deleteTechnicUseCase: DeleteTechnicUseCase,
    ): YandexMapViewModelFactory = YandexMapViewModelFactory(
        getTechnicsUseCase = getTechnicsUseCase,
        saveTechnicUseCase = saveTechnicUseCase,
        deleteAllUseCase = deleteAllUseCase,
        deleteTechnicUseCase = deleteTechnicUseCase
    )
}
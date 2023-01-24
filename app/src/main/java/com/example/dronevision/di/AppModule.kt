package com.example.dronevision.di

import android.content.Context
import com.example.dronevision.domain.use_cases.*
import com.example.dronevision.presentation.ui.MainViewModelFactory
import com.example.dronevision.presentation.ui.subscribers.SubscriberViewModelFactory
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidViewModelFactory
import com.example.dronevision.presentation.ui.targ.TargetViewModelFactory
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
        saveSessionStateUseCase: SaveSessionStateUseCase,
        getSessionStateUseCase: GetSessionStateUseCase
    ): OsmdroidViewModelFactory = OsmdroidViewModelFactory(
        getTechnicsUseCase = getTechnicsUseCase,
        saveTechnicUseCase = saveTechnicUseCase,
        deleteAllUseCase = deleteAllUseCase,
        deleteTechnicUseCase = deleteTechnicUseCase,
        saveSessionStateUseCase = saveSessionStateUseCase,
        getSessionStateUseCase = getSessionStateUseCase
    )
    
    @Provides
    fun provideMainViewModelFactory(
        saveSessionStateUseCase: SaveSessionStateUseCase,
        getSessionStateUseCase: GetSessionStateUseCase,
        getIdUseCase: GetIdUseCase,
        socketUseCase: SocketUseCase
    ): MainViewModelFactory = MainViewModelFactory(
        saveSessionStateUseCase = saveSessionStateUseCase,
        getSessionStateUseCase = getSessionStateUseCase,
        getIdUseCase = getIdUseCase,
        socketUseCase = socketUseCase
    )

    @Provides
    fun provideSubscriberViewModelFactory(
        saveSubscriberUseCase: SaveSubscriberUseCase,
        getSubscribersUseCase: GetSubscribersUseCase,
        removeSubscriberUseCase: RemoveSubscriberUseCase
    ): SubscriberViewModelFactory = SubscriberViewModelFactory(
        saveSubscriberUseCase = saveSubscriberUseCase,
        getSubscribersUseCase = getSubscribersUseCase,
        removeSubscriberUseCase = removeSubscriberUseCase
    )

    @Provides
    fun provideTargetViewModelFactory(
        socketUseCase: SocketUseCase
    ): TargetViewModelFactory = TargetViewModelFactory(socketUseCase = socketUseCase)
}
package com.example.dronevision.di

import com.example.dronevision.presentation.ui.MainActivity
import com.example.lifecalendar.di.DataModule
import com.example.lifecalendar.di.DomainModule
import dagger.Component

@Component(modules = [AppModule::class, DataModule::class, DomainModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
}
package com.example.dronevision.di

import com.example.dronevision.presentation.ui.MainActivity
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidFragment
import dagger.Component

@Component(modules = [AppModule::class, DataModule::class, DomainModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(osmdroidFragment: OsmdroidFragment)
}
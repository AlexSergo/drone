package com.example.dronevision.di

import com.example.dronevision.presentation.ui.MainActivity
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidFragment
import com.example.dronevision.presentation.ui.yandex_map.YandexMapFragment
import dagger.Component

@Component(modules = [AppModule::class, DataModule::class, DomainModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(yandexMapFragment: YandexMapFragment)
    fun inject(osmdroidFragment: OsmdroidFragment)
}
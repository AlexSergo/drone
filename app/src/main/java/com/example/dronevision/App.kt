package com.example.dronevision

import android.app.Application
import com.example.dronevision.di.AppComponent
import com.example.dronevision.di.AppModule
import com.example.dronevision.di.DaggerAppComponent

class App : Application() {
    
    lateinit var appComponent: AppComponent
    
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(context = this))
            .build()
    }
}
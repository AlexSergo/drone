package com.example.dronevision

import android.app.Application
import com.example.dronevision.di.AppComponent
import com.example.dronevision.di.AppModule
import com.example.dronevision.di.DaggerAppComponent
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    
    lateinit var appComponent: AppComponent
    
    override fun onCreate() {
        super.onCreate()
    
        MapKitFactory.setApiKey("21d592db-23af-489e-a87f-cf284dd7d62e")
        MapKitFactory.initialize(this)
        
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(context = this))
            .build()
    }
}
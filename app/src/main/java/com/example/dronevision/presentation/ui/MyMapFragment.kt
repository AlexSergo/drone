package com.example.dronevision.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.App
import com.example.dronevision.presentation.delegates.*
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidFragment
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidViewModel
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidViewModelFactory
import com.example.dronevision.presentation.ui.yandex_map.YandexMapFragment
import com.example.dronevision.presentation.ui.yandex_map.YandexMapViewModel
import com.example.dronevision.presentation.ui.yandex_map.YandexMapViewModelFactory
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

open class MyMapFragment: Fragment(),
    RemoteDatabaseHandler by RemoteDatabaseHandlerImpl(),
    OfflineMapHandler by OfflineMapHandlerImpl(),
    StoragePermissionHandler by StoragePermissionHandlerImpl(),
    GeoInformationHandler by GeoInformationHandlerImpl(),
    LocationDialogHandler by LocationDialogHandlerImpl(),
    ManipulatorSetuper by ManipulatorSetuperImpl() {
    
    protected lateinit var yandexMapViewModel: YandexMapViewModel
    protected lateinit var osmdroidViewModel: OsmdroidViewModel
    protected lateinit var databaseRef: DatabaseReference
    
    @Inject
    lateinit var yandexMapViewModelFactory: YandexMapViewModelFactory
    
    @Inject
    lateinit var osmdroidViewModelFactory: OsmdroidViewModelFactory
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database =
            Firebase.database("https://drone-6c66c-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseRef = database.getReference("message")
    }
    
    protected fun inject(fragment: YandexMapFragment) {
        (requireContext().applicationContext as App).appComponent.inject(fragment)
        yandexMapViewModel =
            ViewModelProvider(this, yandexMapViewModelFactory)[YandexMapViewModel::class.java]
    }

    protected fun inject(fragment: OsmdroidFragment) {
        (requireContext().applicationContext as App).appComponent.inject(fragment)
        osmdroidViewModel =
            ViewModelProvider(this, osmdroidViewModelFactory)[OsmdroidViewModel::class.java]
    }
}

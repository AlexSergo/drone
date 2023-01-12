package com.example.dronevision.presentation.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.App
import com.example.dronevision.presentation.delegates.*
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidFragment
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidViewModel
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidViewModelFactory
import javax.inject.Inject

open class MyMapFragment: Fragment(),
    RemoteDatabaseHandler by RemoteDatabaseHandlerImpl(),
    OfflineMapHandler by OfflineMapHandlerImpl(),
    PermissionHandler by PermissionHandlerImpl(),
    GeoInformationHandler by GeoInformationHandlerImpl(),
    LocationDialogHandler by LocationDialogHandlerImpl(),
    ManipulatorSetuper by ManipulatorSetuperImpl() {
    
    protected lateinit var osmdroidViewModel: OsmdroidViewModel
    
    @Inject
    lateinit var osmdroidViewModelFactory: OsmdroidViewModelFactory

    protected fun inject(fragment: OsmdroidFragment) {
        (requireContext().applicationContext as App).appComponent.inject(fragment)
        osmdroidViewModel =
            ViewModelProvider(this, osmdroidViewModelFactory)[OsmdroidViewModel::class.java]
    }
}

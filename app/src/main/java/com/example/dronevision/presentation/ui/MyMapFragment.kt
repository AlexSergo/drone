package com.example.dronevision.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.App
import com.example.dronevision.presentation.delegates.*
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidFragment
import com.example.dronevision.presentation.ui.view_model.TargetViewModel
import com.example.dronevision.presentation.ui.view_model.TargetViewModelFactory
import com.example.dronevision.presentation.ui.yandex_map.YandexMapFragment
import com.example.dronevision.presentation.ui.yandex_map.TechnicViewModel
import com.example.dronevision.presentation.ui.yandex_map.TechnicViewModelFactory
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import org.osmdroid.util.GeoPoint
import javax.inject.Inject
import kotlin.math.roundToInt

open class MyMapFragment<T>: Fragment(),
    RemoteDatabaseHandler by RemoteDatabaseHandlerImpl(),
    OfflineMapHandler by OfflineMapHandlerImpl(),
    StoragePermissionHandler by StoragePermissionHandlerImpl(),
    GeoInformation by GeoInformationImpl(){

    protected lateinit var viewModel: TechnicViewModel
    protected lateinit var targetViewModel: TargetViewModel
    protected lateinit var databaseRef: DatabaseReference
    protected val listOfTechnic = mutableListOf<T>()

    @Inject
    lateinit var viewModelFactory: TechnicViewModelFactory
    @Inject
    lateinit var targetViewModelFactory: TargetViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database =
            Firebase.database("https://drone-6c66c-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseRef = database.getReference("message")
    }

    protected fun inject(fragment: YandexMapFragment) {
        (requireContext().applicationContext as App).appComponent.inject(fragment)
        initViewModel()
    }

    protected fun inject(fragment: OsmdroidFragment) {
        (requireContext().applicationContext as App).appComponent.inject(fragment)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)[TechnicViewModel::class.java]
        targetViewModel = ViewModelProvider(this, targetViewModelFactory)[TargetViewModel::class.java]
    }

    protected fun getDistance(from: Point, to: Point): Double{
        return (Geo.distance(from, to) / 100).roundToInt() / 10.0
    }

    protected fun getDistance(from: GeoPoint, to: GeoPoint): Double{
        return (Geo.distance(
            Point(from.latitude, from.longitude),
            Point(to.latitude, to.longitude)) / 100).roundToInt() / 10.0
    }
}

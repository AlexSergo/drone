package com.example.dronevision.presentation.ui

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dronevision.AbonentDialogFragment
import com.example.dronevision.R
import com.example.dronevision.data.repository.RepositoryInitializer
import com.example.dronevision.databinding.ActivityMainBinding
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.bluetooth.*
import com.example.dronevision.presentation.view_model.TechnicViewModel
import com.example.dronevision.presentation.view_model.ViewModelFactory
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.RotationType
import com.yandex.runtime.image.ImageProvider
import java.io.IOException

class MainActivity : AppCompatActivity(), BluetoothReceiver.MessageListener,
    NavigationView.OnNavigationItemSelectedListener {
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mapKit: MapKit
    private lateinit var marker: PlacemarkMapObject
    
    lateinit var bluetoothConnection: BluetoothConnection
    private var dialog: SelectBluetoothFragment? = null

    private lateinit var viewModel: TechnicViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupBluetooth()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initMap()
        initMarker()
        initViewModel()
        initTechnic()
        
        setupOptionsMenu()
        setupNavController()
    }

    private fun initTechnic() {
        viewModel.getTechnics()
        viewModel.getLiveData().observe(this, Observer {
            it?.let { list ->
                list.forEach { technic->
                    val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                    val mark = mapObjCollection.addPlacemark(
                        Point(technic.coords.x, technic.coords.y),
                        ImageProvider.fromResource(applicationContext, ImageTypes.imageMap[technic.type]!!)
                    )
                }
            }
        })
    }

    private fun initViewModel(){
        viewModelFactory = ViewModelFactory(RepositoryInitializer.getRepository(this))
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(TechnicViewModel::class.java)
    }
    
    private fun initMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mapKit = MapKitFactory.getInstance()
        val location = mapKit.createUserLocationLayer(binding.mapView.mapWindow)
        location.isVisible = true
    
        // Создание пользователя на карте пока не отображаем ибо надо TODO: создать отдельное активити с запросами на разрешение
//        MapKitFactory.getInstance().createUserLocationLayer(binding.mapView.mapWindow).isVisible = true
    }

    private fun initMarker(){
        marker = binding.mapView.map.mapObjects.addPlacemark(
            Point(0.0, 0.0),
            ImageProvider.fromResource(this, R.drawable.gps_tacker2)
        )
        marker.setIconStyle(IconStyle().setRotationType(RotationType.ROTATE))
        marker.isVisible = false
    }
    
    private fun setupNavController() {
        setSupportActionBar(binding.appBarMain.toolbar)
        
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
    
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.targ01, R.id.targ04, R.id.targ08, R.id.targ10, R.id.targ12, R.id.targ14,
                R.id.targ17, R.id.targ19, R.id.targ20, R.id.targ21, R.id.targ22, R.id.targ23,
                R.id.targ24, R.id.targ25, R.id.targ27, R.id.targ29, R.id.targ30, R.id.targ31,
                R.id.breach, R.id.targ99
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    
        navView.setNavigationItemSelectedListener(this)
    }

    private fun spawnTechnic( @DrawableRes imageRes: Int, type: TechnicTypes){
        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
        val mark = mapObjCollection.addPlacemark(
            Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
            ImageProvider.fromResource(applicationContext, imageRes)
        )
        viewModel.getTechnics()
        var count = 0
        viewModel.getLiveData().observe(this, Observer {
            it?.let {
                count = it.size + 1
            viewModel.saveTechnic(Technic(
                id = count,
                type = type,
                Coordinates(x = mark.geometry.latitude, y = mark.geometry.longitude)
            ))
            }
        })
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.targ01 -> {
                spawnTechnic(R.drawable.ic_01, TechnicTypes.LAUNCHER)
            }
            R.id.targ04 -> {
                spawnTechnic(R.drawable.ic_04, TechnicTypes.OVERLAND)
            }
            R.id.targ08 -> {
                spawnTechnic(R.drawable.ic_08, TechnicTypes.ARTILLERY)
            }
            R.id.targ10 -> {
                spawnTechnic(R.drawable.ic_10, TechnicTypes.REACT)
            }
            R.id.targ12 -> {
                spawnTechnic(R.drawable.ic_12, TechnicTypes.MINES)
            }
            R.id.targ14 -> {
                spawnTechnic(R.drawable.ic_14, TechnicTypes.ZUR)
            }
            R.id.targ17 -> {
                spawnTechnic(R.drawable.ic_17, TechnicTypes.RLS)
            }
            R.id.targ19 -> {
                spawnTechnic(R.drawable.ic_19, TechnicTypes.INFANTRY)
            }
            R.id.targ20 -> {
                spawnTechnic(R.drawable.ic_20, TechnicTypes.O_POINT)
            }
            R.id.targ21 -> {
                spawnTechnic(R.drawable.ic_21, TechnicTypes.KNP)
            }
            R.id.targ22 -> {
                spawnTechnic(R.drawable.ic_22, TechnicTypes.TANKS)
            }
            R.id.targ23 -> {
                spawnTechnic(R.drawable.ic_23, TechnicTypes.BTR)
            }
            R.id.targ24 -> {
                spawnTechnic(R.drawable.ic_24, TechnicTypes.BMP)
            }
            R.id.targ25 -> {
                spawnTechnic(R.drawable.ic_25, TechnicTypes.HELICOPTER)
            }
            R.id.targ27 -> {
                spawnTechnic(R.drawable.ic_27, TechnicTypes.PTRK)
            }
            R.id.targ29 -> {
                spawnTechnic(R.drawable.ic_29, TechnicTypes.KLN_PESH)
            }
            R.id.targ30 -> {
                spawnTechnic(R.drawable.ic_30, TechnicTypes.KLN_BR)
            }
            R.id.targ31 -> {
                spawnTechnic(R.drawable.ic_31, TechnicTypes.TANK)
            }
            R.id.targ99 -> {
                spawnTechnic(R.drawable.ic_99, TechnicTypes.ANOTHER)
            }
            R.id.breach -> {
                spawnTechnic(R.drawable.ic_breach, TechnicTypes.GAP)
            }
        }
        return true
    }
    
    private fun setupBluetooth() {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothConnection = BluetoothConnection(
            bluetoothAdapter,
            context = this, listener = this
        )
        
        dialog = SelectBluetoothFragment(bluetoothAdapter, object : BluetoothCallback {
            override fun onClick(item: BluetoothListItem) {
                item.let {
                    bluetoothConnection.connect(it.mac)
                   // bluetoothConnection.sendMessage("Тест")
                }
                dialog?.dismiss()
            }
        })
    }

    fun addMarker(
        latitude: Double,
        longitude: Double,
        asim: Float,
        @DrawableRes imageRes: Int,
        userData: Any? = null
    ): PlacemarkMapObject {
        marker.direction = asim
        marker.geometry = Point(latitude, longitude)
        marker.userData = userData
        marker.isVisible = true
        marker.addTapListener { mapObject, point ->
            return@addTapListener true
        }
       // markerTapListener?.let { marker.addTapListener(it) }
        return marker
    }
    
    /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
         menuInflater.inflate(R.menu.main, menu)
         return true
     }*/
    
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    
    private fun setupOptionsMenu() {
        val menuHost: MenuHost = this
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.bluetooth -> {
                        dialog?.show(supportFragmentManager, "ActionBottomDialog")
                        true
                    }
                    R.id.actionGeoLocation -> {
                        showLocationDialog()
                        true
                    }
                    R.id.abonentAddItem ->{
                        val abonentDialogFragment = AbonentDialogFragment()
                        abonentDialogFragment.show(supportFragmentManager, "myDialog")
                        true
                    }
                    R.id.removeAll ->{
                        binding.mapView.map.mapObjects.clear()
                        viewModel.deleteAll()
                        initMarker()
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)
    }
    
    private fun showLocationDialog() {
        val locationDialogArray = arrayOf(
            "Запросить у Р-187-П1",
            "Запросить у Android",
            "Снять с карты",
            "Найти на карте",
            "Передать Р-187-П1"
        )
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Координаты")
            .setItems(locationDialogArray) { dialog, which ->
                when (which) {
                    0 -> {}
                    1 -> {}
                    2 -> {}
                    3 -> {
                        checkLocationPermission()
                    }
                    4 -> {}
                }
            }
            .show()
    }
    
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // when permission is already grant
            checkGPS()
        } else {
            // when permission is denied
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }
    
    private fun checkGPS() {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000
        
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()
        val res = LocationServices.getSettingsClient(this.applicationContext)
            .checkLocationSettings(builder)
        
        res.addOnCompleteListener { task ->
            try {
                // when the GPS is on
                task.getResult(
                    ApiException::class.java
                )
                getUserLocation()
            } catch (e: ApiException) {
                // when the GPS is OFF
                e.printStackTrace()
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // here we send the request for enable the GPS
                        val resolveApiException = e as ResolvableApiException
                        resolveApiException.startResolutionForResult(this, 200)
                    } catch (sendIntentException: IntentSender.SendIntentException) {
                    
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // when the setting is unavailable
                        
                    }
                }
            }
        }
    }

    private fun showLocationFromDrone(message: String){
        val array = message.split(",")
        val lat = array[0].toDouble()
        val lon = array[1].toDouble()
        val alt = array[2].toDouble()
        val asim = array[3].toFloat()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
       /* fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val location = task.result

            if (location != null) {
                try {
                    binding.mapView.map.move(
                        CameraPosition(
                            Point(lat, lon),
                            12.0f,
                            asim,
                            0.0f
                        ),
                        Animation(Animation.Type.SMOOTH, 1.0f), null
                    )
                } catch (e: IOException) {

                }
            }
        }*/
        addMarker(lat, lon,asim, R.drawable.gps_tacker2)
    }
    
    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Log.d("getUserLocation", "getUserLocation")
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val location = task.result
            
            if (location != null) {
                try {
                    Log.d("location", "${location.latitude} ${location.longitude}")
                    binding.mapView.map.move(
                        CameraPosition(
                            Point(marker.geometry.latitude, marker.geometry.longitude),
                            12.0f,
                            0.0f,
                            0.0f
                        ),
                        Animation(Animation.Type.SMOOTH, 1.0f), null
                    )
                } catch (e: IOException) {
                
                }
            }
        }
    }
    
    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }
    
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onReceive(message: Message) {
        runOnUiThread {
            Toast.makeText(this, message.message, Toast.LENGTH_LONG).show()
            if (!message.isSystem)
                showLocationFromDrone(message.message)
        }
    }
}

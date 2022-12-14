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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dronevision.AbonentDialogFragment
import com.example.dronevision.R
import com.example.dronevision.databinding.ActivityMainBinding
import com.example.dronevision.presentation.ui.bluetooth.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.runtime.image.ImageProvider
import java.io.IOException

class MainActivity : AppCompatActivity(), BluetoothReceiver.MessageListener,
    NavigationView.OnNavigationItemSelectedListener {
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    
    lateinit var bluetoothConnection: BluetoothConnection
    private var dialog: SelectBluetoothFragment? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupBluetooth()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initMap()
        
        setupOptionsMenu()
        setupNavController()
    }
    
    private fun initMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    
        // Создание пользователя на карте пока не отображаем ибо надо TODO: создать отдельное активити с запросами на разрешение
//        MapKitFactory.getInstance().createUserLocationLayer(binding.mapView.mapWindow).isVisible = true
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
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.targ01 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_01)
                )
            }
            R.id.targ04 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_04)
                )
            }
            R.id.targ08 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_08)
                )
            }
            R.id.targ10 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_10)
                )
            }
            R.id.targ12 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_12)
                )
            }
            R.id.targ14 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_14)
                )
            }
            R.id.targ17 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_17)
                )
            }
            R.id.targ19 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_19)
                )
            }
            R.id.targ20 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_20)
                )
            }
            R.id.targ21 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_21)
                )
            }
            R.id.targ22 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_22)
                )
            }
            R.id.targ23 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_23)
                )
            }
            R.id.targ24 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_24)
                )
            }
            R.id.targ25 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_25)
                )
            }
            R.id.targ27 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_27)
                )
            }
            R.id.targ29 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_29)
                )
            }
            R.id.targ30 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_30)
                )
            }
            R.id.targ31 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_31)
                )
            }
            R.id.targ99 -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_99)
                )
            }
            R.id.breach -> {
                val cameraPositionTarget = binding.mapView.map.cameraPosition.target
                val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                mapObjCollection.addPlacemark(
                    Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                    ImageProvider.fromResource(applicationContext, R.drawable.ic_breach)
                )
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
                            Point(location.latitude, location.longitude),
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

    override fun onReceive(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
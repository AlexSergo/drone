package com.example.dronevision.presentation

import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.loader.content.AsyncTaskLoader
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dronevision.R
import com.example.dronevision.databinding.ActivityMainBinding
import com.example.dronevision.data.RepositoryInitializer
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.Technic
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.view_model.TechnicViewModel
import com.example.dronevision.presentation.view_model.ViewModelFactory
import com.google.android.material.navigation.NavigationView
import com.yandex.mapkit.MapKitFactory
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var dialog: SelectBluetoothFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        MapKitFactory.setApiKey("21d592db-23af-489e-a87f-cf284dd7d62e")
        MapKitFactory.initialize(this)

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        dialog = SelectBluetoothFragment(bluetoothAdapter)
        
        binding = ActivityMainBinding.inflate(layoutInflater)



        setContentView(binding.root)
    
        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, MapFragment())
                .commitNow()
        }*/
        setSupportActionBar(binding.appBarMain.toolbar)
    
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.targ01, R.id.targ04, R.id.targ08,
                R.id.targ10, R.id.targ12, R.id.targ14,
                R.id.targ17, R.id.targ19, R.id.targ20,
                R.id.targ21, R.id.targ22, R.id.targ23,
                R.id.targ24, R.id.targ25, R.id.targ27,
                R.id.targ29, R.id.targ30, R.id.targ31,
                R.id.targ99, R.id.breach
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        dialog?.show(supportFragmentManager, "ActionBottomDialog")
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    
}
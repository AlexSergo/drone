package com.example.dronevision.presentation.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dronevision.App
import com.example.dronevision.R
import com.example.dronevision.databinding.ActivityMainBinding
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.ui.yandex_map.YandexMapViewModel
import com.example.dronevision.presentation.view_model.TechnicViewModel
import com.example.dronevision.presentation.view_model.ViewModelFactory
import com.example.dronevision.utils.SpawnTechnic
import com.example.dronevision.utils.SpawnTechnicModel
import com.google.android.material.navigation.NavigationView
import javax.inject.Inject

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TechnicViewModel
    
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[TechnicViewModel::class.java]
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavController()
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
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_01, TechnicTypes.LAUNCHER))
            }
            /*R.id.targ04 -> {
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
            }*/
        }
        return true
    }
    
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

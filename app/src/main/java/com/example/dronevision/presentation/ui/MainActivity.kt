package com.example.dronevision.presentation.ui

import android.app.Activity
import android.bluetooth.BluetoothManager
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dronevision.AbonentDialogFragment
import com.example.dronevision.App
import com.example.dronevision.R
import com.example.dronevision.databinding.ActivityMainBinding
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.model.BluetoothListItem
import com.example.dronevision.presentation.model.Message
import com.example.dronevision.presentation.ui.bluetooth.*
import com.example.dronevision.presentation.ui.yandex_map.YandexMapFragment
import com.example.dronevision.presentation.view_model.TechnicViewModel
import com.example.dronevision.presentation.view_model.ViewModelFactory
import com.example.dronevision.utils.HgtLoader
import com.example.dronevision.utils.SpawnTechnicModel
import com.google.android.material.navigation.NavigationView
import javax.inject.Inject

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,  BluetoothReceiver.MessageListener {
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TechnicViewModel
    private lateinit var map: IMap
    private var dialog: SelectBluetoothFragment? = null
    
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[TechnicViewModel::class.java]
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOptionsMenu()
        setupBluetooth()
        setupNavController()


        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        map = navFragment?.childFragmentManager?.fragments?.get(0) as YandexMapFragment
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

    private fun setupBluetooth() {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val bluetoothConnection = BluetoothConnection(
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

    override fun onReceive(message: Message, entities: List<Entity>?) {
        runOnUiThread {
            Toast.makeText(this, message.message, Toast.LENGTH_LONG).show()
            if (entities != null)
                map.showLocationFromDrone(entities)
        }
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.targ01 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_01, TechnicTypes.LAUNCHER))
            }
            R.id.targ04 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_04, TechnicTypes.OVERLAND))
            }
            R.id.targ08 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_08, TechnicTypes.ARTILLERY))
            }
            R.id.targ10 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_10, TechnicTypes.REACT))
            }
            R.id.targ12 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_12, TechnicTypes.MINES))
            }
            R.id.targ14 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_14, TechnicTypes.ZUR))
            }
            R.id.targ17 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_17, TechnicTypes.RLS))
            }
            R.id.targ19 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_19, TechnicTypes.INFANTRY))
            }
            R.id.targ20 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_20, TechnicTypes.O_POINT))
            }
            R.id.targ21 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_21, TechnicTypes.KNP))
            }
            R.id.targ22 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_22, TechnicTypes.TANKS))
            }
            R.id.targ23 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_23, TechnicTypes.BTR))
            }
            R.id.targ24 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_24, TechnicTypes.BMP))
            }
            R.id.targ25 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_25, TechnicTypes.HELICOPTER))
            }
            R.id.targ27 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_27, TechnicTypes.PTRK))
            }
            R.id.targ29 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_29, TechnicTypes.KLN_PESH))
            }
            R.id.targ30 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_30, TechnicTypes.KLN_BR))
            }
            R.id.targ31 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_31, TechnicTypes.TANK))
            }
            R.id.targ99 -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_99, TechnicTypes.ANOTHER))
            }
            R.id.breach -> {
                viewModel.spawnTechnic(SpawnTechnicModel(R.drawable.ic_breach, TechnicTypes.GAP))
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
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
                        map.showLocationDialog()
                        true
                    }
                    R.id.abonentAddItem -> {
                        val abonentDialogFragment = AbonentDialogFragment()
                        abonentDialogFragment.show(supportFragmentManager, "myDialog")
                        true
                    }
                    R.id.removeAll -> {
                        map.deleteAll()
                        true
                    }
                    R.id.addHeightMaps -> {
                        //  Вставить код для загрузка высотных карт
                        val hgtLoader = HgtLoader(resources)
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

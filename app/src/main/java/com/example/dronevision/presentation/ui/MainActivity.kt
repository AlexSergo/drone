package com.example.dronevision.presentation.ui

import android.bluetooth.BluetoothManager
import android.os.Bundle
import android.preference.PreferenceManager
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
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dronevision.AbonentDialogFragment
import com.example.dronevision.R
import com.example.dronevision.databinding.ActivityMainBinding
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.model.BluetoothListItem
import com.example.dronevision.presentation.model.Message
import com.example.dronevision.presentation.ui.bluetooth.*

import com.example.dronevision.utils.HgtLoader
import com.google.android.material.navigation.NavigationView
import com.google.firebase.components.BuildConfig
import org.osmdroid.config.Configuration

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener, BluetoothReceiver.MessageListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var map: IMap
    private var dialog: SelectBluetoothFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOptionsMenu()
        setupBluetooth()
        setupNavController()
        
        setupOsmdroidConfiguration()

        val navFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        map = navFragment?.childFragmentManager?.fragments?.get(0) as IMap
    }
    
    private fun setupOsmdroidConfiguration() {
        val provider = Configuration.getInstance()
        provider.userAgentValue = BuildConfig.APPLICATION_ID
        provider.osmdroidTileCache = externalCacheDir
        provider.load(this, PreferenceManager.getDefaultSharedPreferences(this))
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
                }
                dialog?.dismiss()
            }
        })
    }

    override fun onReceive(message: Message, entities: MutableList<Entity>?) {
        entities?.let {
            if (it[0].lat.isNaN() || it[0].lon.isNaN()) {
                it[0] = Entity(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false)
                it[1] = Entity(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false)
            }
        }
        map = getCurrentMap()
        runOnUiThread {
            if (message.isSystem)
                Toast.makeText(this, message.message, Toast.LENGTH_LONG).show()
            if (entities != null)
                map.showDataFromDrone(entities)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        map = getCurrentMap()
        when (item.itemId) {
            R.id.targ01 -> {
                map.spawnTechnic(TechnicTypes.LAUNCHER)
            }
            R.id.targ04 -> {
                map.spawnTechnic(TechnicTypes.OVERLAND)
            }
            R.id.targ08 -> {
                map.spawnTechnic(TechnicTypes.ARTILLERY)
            }
            R.id.targ10 -> {
                map.spawnTechnic(TechnicTypes.REACT)
            }
            R.id.targ12 -> {
                map.spawnTechnic(TechnicTypes.MINES)
            }
            R.id.targ14 -> {
                map.spawnTechnic(TechnicTypes.ZUR)
            }
            R.id.targ17 -> {
                map.spawnTechnic(TechnicTypes.RLS)
            }
            R.id.targ19 -> {
                map.spawnTechnic(TechnicTypes.INFANTRY)
            }
            R.id.targ20 -> {
                map.spawnTechnic(TechnicTypes.O_POINT)
            }
            R.id.targ21 -> {
                map.spawnTechnic(TechnicTypes.KNP)
            }
            R.id.targ22 -> {
                map.spawnTechnic(TechnicTypes.TANKS)
            }
            R.id.targ23 -> {
                map.spawnTechnic(TechnicTypes.BTR)
            }
            R.id.targ24 -> {
                map.spawnTechnic(TechnicTypes.BMP)
            }
            R.id.targ25 -> {
                map.spawnTechnic(TechnicTypes.HELICOPTER)
            }
            R.id.targ27 -> {
                map.spawnTechnic(TechnicTypes.PTRK)
            }
            R.id.targ29 -> {
                map.spawnTechnic(TechnicTypes.KLN_PESH)
            }
            R.id.targ30 -> {
                map.spawnTechnic(TechnicTypes.KLN_BR)
            }
            R.id.targ31 -> {
                map.spawnTechnic(TechnicTypes.TANK)
            }
            R.id.targ99 -> {
                map.spawnTechnic(TechnicTypes.ANOTHER)
            }
            R.id.breach -> {
                map.spawnTechnic(TechnicTypes.GAP)
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
                map = getCurrentMap()
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
                        //  Загрузка высотной карты (Москвы) надо будет добавить возможность выбирать регионы
                        val hgtLoader = HgtLoader(resources)
                        true
                    }
                    R.id.mapOfflineItem -> {
                        map.offlineMode()
                        true
                    }
                    R.id.mapGridItem ->{
                        menuItem.isChecked = !menuItem.isChecked
                        map.changeGridState(menuItem.isChecked)
                        true
                    }
                    R.id.mapOsmItem ->{
                        findNavController(R.id.nav_host_fragment_content_main)
                            .navigate(R.id.action_yandexMapFragment_to_osmdroidFragment)
                        true
                    }
                    R.id.mapYandexItem ->{
                        findNavController(R.id.nav_host_fragment_content_main)
                            .navigate(R.id.action_osmdroidFragment_to_yandexMapFragment)
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)
    }

    private fun getCurrentMap(): IMap {
        val navFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        var fragment = navFragment?.childFragmentManager?.fragments?.get(0)
        if (fragment != null) {
            if (fragment.isVisible)
                return fragment as IMap
        }
        fragment = navFragment?.childFragmentManager?.fragments?.get(1)
        return fragment as IMap
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

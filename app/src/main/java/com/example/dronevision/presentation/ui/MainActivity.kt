package com.example.dronevision.presentation.ui


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.dronevision.App
import com.example.dronevision.R
import com.example.dronevision.databinding.ActivityMainBinding
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.delegates.BluetoothHandler
import com.example.dronevision.presentation.delegates.BluetoothHandlerImpl
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.model.bluetooth.BluetoothListItem
import com.example.dronevision.presentation.model.bluetooth.Entity
import com.example.dronevision.presentation.ui.bluetooth.BluetoothCallback
import com.example.dronevision.presentation.ui.bluetooth.SelectBluetoothFragment
import com.example.dronevision.presentation.ui.osmdroid_map.IMap
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidFragment
import com.example.dronevision.presentation.ui.subscribers.SubscriberDialogFragment
import com.example.dronevision.presentation.ui.subscribers.SubscriberListDialog
import com.example.dronevision.utils.*
import com.example.dronevision.utils.FileTools.createAppFolder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.components.BuildConfig
import org.osmdroid.config.Configuration
import javax.inject.Inject

class MainActivity : AppCompatActivity(), BluetoothHandler by BluetoothHandlerImpl(),
    NavigationView.OnNavigationItemSelectedListener, MapActivityListener {
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var map: IMap
    private var dialog: SelectBluetoothFragment? = null
    private lateinit var mainViewModel: MainViewModel
    
    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
        createAppFolder()
        initViewModel()
        val id = Device.getDeviceId(applicationContext)
        println(id)
      //  checkRegistration() //TODO: Раскоментить когда будет серверная часть
        setupOptionsMenu()
        setupDrawer()
        setupBluetoothDialog()
        setupOsmdroidConfiguration()
    }

    private fun checkPermissions() {
            val permission1 = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            val permission2 = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.BLUETOOTH_SCAN);
            if (permission1 != PackageManager.PERMISSION_GRANTED) {
                // We do not have permission so immediate the consumer
                ActivityCompat.requestPermissions(
                    this,
                    Constants.PERMISSIONS_STORAGE,
                    1
                );
            } else if (permission2 != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                    this,
                    Constants.PERMISSIONS_LOCATION,
                    1
                );
            }
    }


    private fun initViewModel() {
        (applicationContext as App).appComponent.inject(this)
        mainViewModel =
            ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]
    }

    private fun checkRegistration(){
        var sharedPreferences = SharedPreferences(applicationContext)
        val id = Device.getDeviceId(applicationContext)
        if (id != null) {
            val hash = sharedPreferences.getValue("AUTH_TOKEN")
            if (hash == null) {
                mainViewModel.getId(id)
                mainViewModel.idLiveData.observe(this, Observer {
                    it?.let { hash ->
                        if (hash == Hash.md5(id)) {
                            sharedPreferences.save("AUTH_TOKEN", hash)
                            setupOptionsMenu()
                            setupDrawer()
                            setupBluetoothDialog()
                        }
                    }
                })
            }
            else
                if (hash == Hash.md5(id)) {
                    setupOptionsMenu()
                    setupDrawer()
                    setupBluetoothDialog()
                }
        }
    }
    
    private fun setupBluetoothDialog() {
        val connection = setupBluetooth(
            context = this,
            systemService = getSystemService(BLUETOOTH_SERVICE),
            listener = this)

        dialog = SelectBluetoothFragment(connection.getAdapter(), object : BluetoothCallback {
            override fun onClick(item: BluetoothListItem) {
                item.let { connection.connect(it.mac) }
                dialog?.dismiss()
            }
        })
    }

    override fun showMessage(message: String){
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun showDroneData(entities: MutableList<Entity>){
        runOnUiThread {
            if (entities[0].lat.isNaN() || entities[0].lon.isNaN()) {
                entities[0] = Entity(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false)
                entities[1] = Entity(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, false)
            }
            map.showDataFromDrone(entities)
        }
    }

    override fun receiveDeviceId(id: String){
        runOnUiThread {
            val subscriberDialogFragment = SubscriberDialogFragment(id)
            subscriberDialogFragment.show(supportFragmentManager, "")
        }
    }

    override fun receiveTarget(technic: Technic){
        runOnUiThread {
            map.spawnTechnic(technic.type, Coordinates(x = technic.coords.x, y = technic.coords.y))
        }
    }

    private fun setupOsmdroidConfiguration() {
        val provider = Configuration.getInstance()
        provider.userAgentValue = BuildConfig.APPLICATION_ID
        provider.osmdroidTileCache = externalCacheDir
        provider.load(this, PreferenceManager.getDefaultSharedPreferences(this))
    }

    private fun setupDrawer(){
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false);

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
    
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    
        val button = findViewById<ImageButton>(R.id.drawerButton)
        button.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    
        navView.setNavigationItemSelectedListener(this)
        navController = findNavController(R.id.nav_host_fragment_content_main)
        initMap()
    }
    
    private fun initMap() {
        val navFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        val fragment = navFragment?.childFragmentManager?.fragments?.get(0)
        map = fragment as IMap
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
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
                setupMenuState(menu)
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
                    R.id.subscriberAddItem -> {
                        val subscriberDialogFragment = SubscriberDialogFragment()
                        subscriberDialogFragment.show(supportFragmentManager, "myDialog")
                        true
                    }
                    R.id.subscriberListItem -> {
                        val subscriberListDialogFragment = SubscriberListDialog()
                        subscriberListDialogFragment.show(supportFragmentManager, "listDialog")
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
                        map.setMapType(MapType.OFFLINE.value)
                        true
                    }
                    R.id.mapGridItem -> {
                        if (map is OsmdroidFragment) menuItem.isChecked = !menuItem.isChecked
                        map.changeGridState(menuItem.isChecked)
                        true
                    }
                    R.id.mapOsmItem -> {
                        map.setMapType(MapType.OSM.value)
                        true
                    }
                    R.id.mapSchemeItem -> {
                        map.setMapType(MapType.SCHEME_MAP.value)
                        true
                    }
                    R.id.mapHybridItem -> {
                        map.setMapType(MapType.GOOGLE_HYB.value)
                        true
                    }
                    R.id.mapSatelliteItem -> {
                        map.setMapType(MapType.GOOGLE_SAT.value)
                        true
                    }
                    R.id.Id ->{
                        val androidIdDialog = AndroidIdFragment(dialog)

                        androidIdDialog.show(supportFragmentManager, "id_dialog")
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.RESUMED)
    }
    
    private fun setupMenuState(menu: Menu) {
        mainViewModel.getSessionState()
        val mapGridItem = menu.findItem(R.id.mapGridItem)
        mainViewModel.sessionStateLiveData.observe(this) { sessionState ->
            mapGridItem.isChecked = sessionState.isGrid
        }
    }
    
    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
}
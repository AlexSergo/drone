package com.example.dronevision.presentation.ui


import android.content.ClipboardManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.preference.PreferenceManager
import com.example.dronevision.App
import com.example.dronevision.R
import com.example.dronevision.data.source.local.prefs.PasswordManager
import com.example.dronevision.data.source.local.prefs.SharedPreferences
import com.example.dronevision.databinding.ActivityMainBinding
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.DownloadController
import com.example.dronevision.presentation.delegates.BluetoothHandler
import com.example.dronevision.presentation.delegates.BluetoothHandlerImpl
import com.example.dronevision.presentation.delegates.DivisionHandler
import com.example.dronevision.presentation.delegates.DivisionHandlerImpl
import com.example.dronevision.presentation.mapper.TechnicMapperUI
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.model.bluetooth.BluetoothListItem
import com.example.dronevision.presentation.model.bluetooth.Entity
import com.example.dronevision.presentation.ui.auth.AuthDialog
import com.example.dronevision.presentation.ui.auth.AuthDialogCallback
import com.example.dronevision.presentation.ui.bluetooth.BluetoothCallback
import com.example.dronevision.presentation.ui.bluetooth.SelectBluetoothFragment
import com.example.dronevision.presentation.ui.osmdroid_map.IMap
import com.example.dronevision.presentation.ui.osmdroid_map.OsmdroidFragment
import com.example.dronevision.presentation.ui.subscribers.SubscriberDialogFragment
import com.example.dronevision.presentation.ui.subscribers.SubscriberListDialog
import com.example.dronevision.presentation.ui.subscribers.SubscribersType
import com.example.dronevision.utils.*
import com.example.dronevision.utils.FileTools.createAppFolder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.components.BuildConfig
import org.osmdroid.config.Configuration
import javax.inject.Inject


class MainActivity : AppCompatActivity(), BluetoothHandler by BluetoothHandlerImpl(),
    DivisionHandler by DivisionHandlerImpl(),
    NavigationView.OnNavigationItemSelectedListener, MapActivityListener, OpenDialogCallback {
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var map: IMap
    private var dialog: SelectBluetoothFragment? = null
    private var authDialog: AuthDialog? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var downloadController: DownloadController
    
    @Inject
    lateinit var mainViewModelFactory: MainViewModelFactory
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Device.getDeviceId(applicationContext)
//        auth()
        
        downloadController = DownloadController(this)
        PermissionTools.checkAndRequestPermissions(this)
        createAppFolder()
        initViewModel()
        setupOptionsMenu()
        setupDrawer()
        setupBluetoothDialog()
        setupOsmdroidConfiguration()
        
    }
    
    private fun auth() {
        authDialog = AuthDialog(object : AuthDialogCallback {
            override fun checkRegistration(id: String, password: String) {
                try {
                    val sharedPreferences = SharedPreferences(this@MainActivity)
                    val passwordManager = PasswordManager(this@MainActivity)
                    mainViewModel.getId(id, password)
                    mainViewModel.authLiveData.observe(this@MainActivity) {
                        it?.let { remoteId ->
                            if (remoteId == Hash.md5(id + "крокодил")) {
                                sharedPreferences.save("AUTH_TOKEN", remoteId)
                                passwordManager.addPassword(password)
                                authDialog?.dismiss()
                            }
                        }
                    }
                } catch (_: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Невозможно подключиться к серверу!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        authDialog?.isCancelable = false
        authDialog?.show(supportFragmentManager, "auth")
    }

    private fun setupBluetoothDialog() {
        val connection = setupBluetooth(
            context = this,
            systemService = getSystemService(BLUETOOTH_SERVICE),
            listener = this
        )
        
        dialog = SelectBluetoothFragment(connection.getAdapter(), object : BluetoothCallback {
            override fun onClick(item: BluetoothListItem) {
                item.let { connection.connect(it.mac) }
                dialog?.dismiss()
            }
        })
    }
    
    override fun showMessage(message: String) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }
    
    override fun showDroneData(entities: MutableList<Entity>) {
        runOnUiThread {
            if (entities[0].lat.isNaN() || entities[0].lon.isNaN()) {
                entities[0] = Entity(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0)
                entities[1] = Entity(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0)
            }
            map.showDataFromDrone(entities)
        }
    }
    
    override fun receiveDeviceId(id: String) {
        runOnUiThread {
            val subscriberDialogFragment = SubscriberDialogFragment(id)
            subscriberDialogFragment.show(supportFragmentManager, "")
        }
    }
    
    override fun receiveTechnic(technic: Technic) {
        runOnUiThread {
            technic.division?.let {
                map.spawnTechnic(
                    technic.technicTypes,
                    Coordinates(x = technic.coordinates.x, y = technic.coordinates.y),
                    technic.division
                )
            }
        }
    }
    
    private fun setupOsmdroidConfiguration() {
        val provider = Configuration.getInstance()
        provider.userAgentValue = BuildConfig.APPLICATION_ID
        provider.osmdroidTileCache = externalCacheDir
        provider.load(this, PreferenceManager.getDefaultSharedPreferences(this))
    }
    
    private fun setupDrawer() {
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        
        val toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        
        val toolbarSubstring = findViewById<TextView>(R.id.toolbarSubstring)
        toolbarSubstring.text = BuildConfig.VERSION_NAME + "@svohelp2023"
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
        if (!checkDivision(this))
            return false
        val division = getDivision(this)!!
        when (item.itemId) {
            R.id.targ01 -> {
                map.spawnTechnic(TechnicTypes.LAUNCHER, division = division)
            }
            R.id.targ04 -> {
                map.spawnTechnic(TechnicTypes.OVERLAND, division = division)
            }
            R.id.targ08 -> {
                map.spawnTechnic(TechnicTypes.ARTILLERY, division = division)
            }
            R.id.targ10 -> {
                map.spawnTechnic(TechnicTypes.REACT, division = division)
            }
            R.id.targ12 -> {
                map.spawnTechnic(TechnicTypes.MINES, division = division)
            }
            R.id.targ14 -> {
                map.spawnTechnic(TechnicTypes.ZUR, division = division)
            }
            R.id.targ17 -> {
                map.spawnTechnic(TechnicTypes.RLS, division = division)
            }
            R.id.targ19 -> {
                map.spawnTechnic(TechnicTypes.INFANTRY, division = division)
            }
            R.id.targ20 -> {
                map.spawnTechnic(TechnicTypes.O_POINT, division = division)
            }
            R.id.targ21 -> {
                map.spawnTechnic(TechnicTypes.KNP, division = division)
            }
            R.id.targ22 -> {
                map.spawnTechnic(TechnicTypes.TANKS, division = division)
            }
            R.id.targ23 -> {
                map.spawnTechnic(TechnicTypes.BTR, division = division)
            }
            R.id.targ24 -> {
                map.spawnTechnic(TechnicTypes.BMP, division = division)
            }
            R.id.targ25 -> {
                map.spawnTechnic(TechnicTypes.HELICOPTER, division = division)
            }
            R.id.targ27 -> {
                map.spawnTechnic(TechnicTypes.PTRK, division = division)
            }
            R.id.targ29 -> {
                map.spawnTechnic(TechnicTypes.KLN_PESH, division = division)
            }
            R.id.targ30 -> {
                map.spawnTechnic(TechnicTypes.KLN_BR, division = division)
            }
            R.id.targ31 -> {
                map.spawnTechnic(TechnicTypes.TANK, division = division)
            }
            R.id.targ99 -> {
                map.spawnTechnic(TechnicTypes.ANOTHER, division = division)
            }
            R.id.breach -> {
                map.spawnTechnic(TechnicTypes.GAP, division = division)
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
                        val subscriberListDialogFragment =
                            SubscriberListDialog(subscribersType = SubscribersType.All)
                        subscriberListDialogFragment.show(supportFragmentManager, "listDialog")
                        true
                    }
                    R.id.removeAll -> {
                        map.deleteAll()
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
                   /* R.id.mapNokiaSatItem -> {
                        map.setMapType(MapType.NOKIA_SAT.value)
                        true
                    }*/
                    R.id.mapCacheDownloadItem -> {
                        map.cacheMap()
                        true
                    }
                    R.id.mapSearchItem -> {
                        map.findGeoPoint()
                        true
                    }
                    R.id.deviceId -> {
                        val androidIdDialog = AndroidIdFragment()
                        
                        androidIdDialog.show(supportFragmentManager, "id_dialog")
                        true
                    }
                    R.id.pasteBuffer -> {
                        val myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                        val abc = myClipboard?.primaryClip
                        val item = abc?.getItemAt(0)
                        val target = TechnicMapperUI.mapTextToTechnicUI(item?.text.toString())
                        target.division?.let {
                            map.spawnTechnic(
                                target.technicTypes,
                                target.coordinates,
                                target.division
                            )
                        }
                        return true
                    }
                    R.id.updates -> {
                        try {
                            downloadController.enqueueDownload()
                            //   mainViewModel.updateApp()
                        } catch (_: Exception) {
                            Toast.makeText(
                                applicationContext,
                                "Невозможно подключиться!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        return true
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
    
    private fun initViewModel() {
        (applicationContext as App).appComponent.inject(this)
        mainViewModel =
            ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]
    }
    
    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    override fun openDialog(dialog: DialogFragment) {
        dialog.show(supportFragmentManager, "")
    }
}
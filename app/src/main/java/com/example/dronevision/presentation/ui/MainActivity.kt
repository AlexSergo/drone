package com.example.dronevision.presentation.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dronevision.App
import com.example.dronevision.R
import com.example.dronevision.databinding.ActivityMainBinding
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.view_model.TechnicViewModel
import com.example.dronevision.presentation.view_model.ViewModelFactory
import com.example.dronevision.utils.SpawnTechnicModel
import com.google.android.material.navigation.NavigationView
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
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

/*        val request = Request.Builder()
            .url("https://127.0.0.1/chat")
            .build()
        val listener = object: WebSocketListener() {
            override fun onMessage(ws: WebSocket, mess: String) {

            }
        }
        val ws = OkHttpClient()
            .newWebSocket(request, listener)

        ws.send(
            JSONObject()
            .put("action", "sendmessage")
            .put("data", "Hello from Android!")
            .toString())*/

        connect()
    }

    private fun connect() {
        val url = Url("127.0.0.1/chat")
        val ktor = HttpClient(CIO) {
            install(WebSockets)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            connect(ktor, url)
        }
    }

    private suspend fun connect(ktor: HttpClient, u: Url) {
        ktor.webSocket(Get, host = "127.0.0.1", port = 8080, path = "/chat") {
            while(true) {
                val othersMessage = incoming.receive() as? Frame.Text
                println(othersMessage?.readText())
                val myMessage = "Hello!"
                if(myMessage != null) {
                    send(myMessage)
                }
            }
        }
        ktor.close()
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
        return true
    }
    
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

package com.example.dronevision

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dronevision.databinding.ActivityMainBinding
import com.yandex.mapkit.MapKitFactory

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        MapKitFactory.setApiKey("21d592db-23af-489e-a87f-cf284dd7d62e")
        MapKitFactory.initialize(this)
        setContentView(view)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, MapFragment())
                .commitNow()
        }
    }
}
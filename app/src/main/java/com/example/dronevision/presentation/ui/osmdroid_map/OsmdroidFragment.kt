package com.example.dronevision.presentation.ui.osmdroid_map

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.dronevision.databinding.FragmentOsmdroidBinding
import com.example.dronevision.presentation.ui.IMap
import com.example.dronevision.presentation.ui.bluetooth.Entity
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.File

class OsmdroidFragment : Fragment(), IMap {
    
    private lateinit var binding: FragmentOsmdroidBinding
    private lateinit var rotationGestureOverlay: RotationGestureOverlay
    private val dirName = Environment.getExternalStorageDirectory().path + "/Drone Vision/"
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOsmdroidBinding.inflate(inflater, container, false)
        
        checkStoragePermissions()
        setupOsmdroidMap()
        
        return binding.root
    }
    
    private fun checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION")
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(
                        String.format(
                            "package:%s",
                            *arrayOf<Any>(requireContext().applicationContext.packageName)
                        )
                    )
                    startActivityForResult(intent, 2296)
                } catch (e: Exception) {
                    val intent2 = Intent()
                    intent2.action = "android.settings.MANAGE_ALL_FILES_ACCESS_PERMISSION"
                    startActivityForResult(intent2, 2296)
                }
            }
        } else if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(
                requireContext(),
                "android.permission.WRITE_EXTERNAL_STORAGE"
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf("android.permission.WRITE_EXTERNAL_STORAGE"),
                1
            )
        }
    }
    
    private fun setupOsmdroidMap() = binding.run {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(18.0)
        val compassOverlay = CompassOverlay(requireContext().applicationContext, mapView)
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)
        
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        
        rotationGestureOverlay = RotationGestureOverlay(mapView)
        mapView.overlays.add(rotationGestureOverlay)
        
        setupCompass()
        setupZoomButtons()
        setupDisplayMetrics()
    }
    
    private fun setupCompass() = binding.run {
        compassButton.setOnClickListener {
            if (!rotationGestureOverlay.isEnabled) {
                rotationGestureOverlay.isEnabled = true
                lockerView.visibility = View.INVISIBLE
            }
            mapView.mapOrientation = 0.0f
            compassButton.rotation = 0.0f
        }
        
        compassButton.setOnLongClickListener {
            if (!rotationGestureOverlay.isEnabled) false
            rotationGestureOverlay.setEnabled(false)
            lockerView.visibility = View.VISIBLE
            true
        }
    }
    
    private fun setupZoomButtons() = binding.run {
        zoomInButton.setOnClickListener { mapView.controller.zoomIn() }
        zoomOutButton.setOnClickListener { mapView.controller.zoomOut() }
    }
    
    private fun setupDisplayMetrics() = binding.run {
        val displayMetrics = resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(mapView)
        scaleBarOverlay.unitsOfMeasure = ScaleBarOverlay.UnitsOfMeasure.metric
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setTextSize(30.0f)
        scaleBarOverlay.setScaleBarOffset(
            displayMetrics.widthPixels / 2,
            displayMetrics.heightPixels - (displayMetrics.density * 70.0f).toInt()
        )
        mapView.overlayManager.add(scaleBarOverlay)
    }
    
    
    override fun showLocationFromDrone(entities: List<Entity>) {
    }
    
    override fun showLocationDialog() {
    }
    
    override fun deleteAll() {
    }
    
    override fun offlineMode() {
        
        val folder = File(dirName)
        if (folder.exists()) {
            val listOfFiles = folder.listFiles()!!
            Log.d("listOfFiles", listOfFiles.size.toString())
            
            if (listOfFiles.isNotEmpty()) {
                val offlineArray: MutableList<String> = ArrayList()
                for (file in listOfFiles) {
                    val isFileMBTiles =
                        file.name.substringAfterLast('.', "") == "mbtiles"
                    if (file.isFile && isFileMBTiles) {
                        offlineArray.add(file.name)
                    }
                }
                if (offlineArray.size != 0) {
                    val items = offlineArray.toTypedArray()
                    val mapOfflineBuilder = AlertDialog.Builder(requireContext())
                    mapOfflineBuilder.setTitle("Файлы БД оффлайн карт" as CharSequence)
                    mapOfflineBuilder.create()
                    mapOfflineBuilder.setItems(items as Array<CharSequence>,
                        DialogInterface.OnClickListener { _, which ->
                            
                            val fileName: String = items[which]
                            val path = Environment.getExternalStorageDirectory().path + "/Drone Vision/" + fileName
                            val exitFile = File(path)
                            try {
                                binding.mapView.setTileProvider(
                                    OfflineTileProvider(
                                        SimpleRegisterReceiver(requireContext()),
                                        arrayOf(exitFile)
                                    )
                                )
                                binding.mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                                binding.mapView.setUseDataConnection(false)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            
                        }).show()
                } else Toast.makeText(
                    requireContext(),
                    "Файлы mbtiles не найдены в $dirName",
                    Toast.LENGTH_LONG
                ).show()
            } else Toast.makeText(
                requireContext(),
                "Файлы не найдены в $dirName",
                Toast.LENGTH_LONG
            ).show()
        } else Toast.makeText(
            requireContext(),
            "Папка хранения $dirName не найдена",
            Toast.LENGTH_LONG
        ).show()
    }
    
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
    
    override fun onDetach() {
        super.onDetach()
        binding.mapView.onDetach()
    }
}
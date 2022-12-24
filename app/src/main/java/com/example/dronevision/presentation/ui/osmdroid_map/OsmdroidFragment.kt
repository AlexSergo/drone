package com.example.dronevision.presentation.ui.osmdroid_map

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.example.dronevision.R
import com.example.dronevision.databinding.FragmentOsmdroidBinding
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.IMap
import com.example.dronevision.presentation.ui.ImageTypes
import com.example.dronevision.presentation.ui.MyMapFragment
import com.example.dronevision.presentation.ui.bluetooth.Entity
import org.osmdroid.events.MapAdapter
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2
import org.osmdroid.views.overlay.Polyline
import java.io.File
import kotlin.math.abs


class OsmdroidFragment : MyMapFragment(), IMap {
    
    private lateinit var binding: FragmentOsmdroidBinding
    private lateinit var rotationGestureOverlay: RotationGestureOverlay
    private val dirName = Environment.getExternalStorageDirectory().path + "/Drone Vision/"

    private val overlayGrid = LatLonGridlineOverlay2()
    private val listOfTechnic = mutableListOf<Overlay>()
    private lateinit var droneMarker: Marker
    private lateinit var polylineToCenter: Polyline
    private var aimMarker: Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOsmdroidBinding.inflate(inflater, container, false)
        
        checkStoragePermissions()
        setupOsmdroidMap()
        initDroneMarker()
        initTechnic()

        setPolyline(polylineToCenter,
            listOf(droneMarker.position,
                GeoPoint(binding.mapView.mapCenter.latitude, binding.mapView.mapCenter.longitude)))
        
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
        mapView.controller.setZoom(2.0)
        val compassOverlay = CompassOverlay(requireContext().applicationContext, mapView)
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)
        
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        
        rotationGestureOverlay = RotationGestureOverlay(mapView)
        mapView.overlays.add(rotationGestureOverlay)

        mapView.addMapListener(object : MapListener{
            override fun onScroll(event: ScrollEvent?): Boolean {
                setPolyline(polylineToCenter,
                    listOf(droneMarker.position,
                        GeoPoint(binding.mapView.mapCenter.latitude, binding.mapView.mapCenter.longitude)))
                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                return true
            }
        })
        
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

    override fun initDroneMarker() {
        droneMarker = Marker(binding.mapView)
        drawMarker(droneMarker, latitude = 10.0, longitude = 0.0, R.drawable.gps_tacker2)

        polylineToCenter = Polyline()
    }

    private fun setPolyline(polyline: Polyline, points: List<GeoPoint>){
        polyline.setPoints(points)
        polyline.color = Color.BLUE
        binding.mapView.overlays.add(polyline)
    }

    override fun initTechnic() {
        viewModel.getTechnics()
        viewModel.technicListLiveData.observe(viewLifecycleOwner) {
            it?.let { list ->
                list.forEach { technic ->

                    val mark = Marker(binding.mapView)
                    drawMarker(mark, technic.coords.x, technic.coords.y,
                        ImageTypes.imageMap[technic.type]!!)

                    listOfTechnic.add(mark)
                   // addClickListenerToMark(mark, technic.type)
                }
            }
        }
    }

    override fun spawnTechnic(@DrawableRes imageRes: Int,
                             type: TechnicTypes,
                             coords: Coordinates?
    ){
        val cameraPosition = binding.mapView.mapCenter
        viewModel.getTechnics()
        var count = 0
        viewModel.technicListLiveData.observe(viewLifecycleOwner) {
            it?.let {
                count = it.size + 1
                val mark: Marker = if (coords != null)
                    setMark(coords.x, coords.y, imageRes)
                else
                    setMark(cameraPosition.latitude, cameraPosition.longitude, imageRes)

                //addClickListenerToMark(mark, type)
                viewModel.saveTechnic(
                    Technic(
                        id = count,
                        type = type,
                        Coordinates(x = mark.position.latitude, y = mark.position.longitude)
                    )
                )
            }
        }
    }

    private fun setMark(
        latitude: Double,
        longitude: Double,
        @DrawableRes imageRes: Int
    ): Marker {
        aimMarker?.let {
            if ( abs(latitude - it.position.latitude) < 0.0001
                && abs(longitude - it.position.longitude) < 0.0001)
                removeAim()
        }
        var mark = Marker(binding.mapView)
        mark = drawMarker(mark, latitude, longitude, imageRes)
        listOfTechnic.add(mark)
        return mark
    }

    private fun drawMarker(mark: Marker, latitude: Double, longitude: Double, @DrawableRes imageRes: Int): Marker{
        mark.position = GeoPoint(latitude, longitude)
        mark.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.mapView.overlays.add(mark)
        mark.icon = getDrawable(requireContext(), imageRes)

        return mark
    }

    override fun removeAim(){
        aimMarker?.remove(binding.mapView)
        aimMarker = null
       // polylineToAim?.parent?.remove(polylineToAim!!)
    }

    override fun showLocationFromDrone(entities: List<Entity>) {
    }
    
    override fun showLocationDialog() {
    }
    
    override fun deleteAll() = binding.run {
        mapView.overlays.removeAll(listOfTechnic)
        viewModel.deleteAll()
        listOfTechnic.clear()
        return@run
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

    override fun changeGridState(isShow: Boolean) {
        if (isShow)
            binding.mapView.overlays.add(overlayGrid)
        else
            binding.mapView.overlays.remove(overlayGrid)
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
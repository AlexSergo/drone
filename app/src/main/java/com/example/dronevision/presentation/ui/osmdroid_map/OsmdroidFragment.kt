package com.example.dronevision.presentation.ui.osmdroid_map

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.IntentSender
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.util.Pair
import androidx.preference.PreferenceManager
import com.example.dronevision.databinding.FragmentOsmdroidBinding
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.delegates.LocationDialogCallback
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.model.bluetooth.Entity
import com.example.dronevision.presentation.ui.MainActivity
import com.example.dronevision.presentation.ui.MyMapFragment
import com.example.dronevision.presentation.ui.targ.TargetFragment
import com.example.dronevision.utils.Device
import com.example.dronevision.utils.ImageTypes
import com.example.dronevision.utils.MapTools
import com.example.dronevision.utils.MapType
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.cachemanager.CacheManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.abs


class OsmdroidFragment : MyMapFragment(), IMap {
    
    private lateinit var binding: FragmentOsmdroidBinding
    private lateinit var rotationGestureOverlay: RotationGestureOverlay
    
    private val overlayGrid = LatLonGridlineOverlay2()
    private lateinit var droneMarker: Marker
    private var aimMarker: Marker? = null
    private lateinit var polylineToCenter: Polyline
    private var polylineToAim: Polyline = Polyline()
    private val listOfTechnic = mutableListOf<Overlay>()
    private var locationOverlay: MyLocationNewOverlay? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(this)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOsmdroidBinding.inflate(inflater, container, false)
        checkStoragePermissions(requireActivity())
        
        setupOsmdroidMap()
        initDroneMarker()
        initTechnic()
        setupLastSessionState()
        setPolyline(
            polylineToCenter,
            listOf(
                droneMarker.position,
                GeoPoint(binding.mapView.mapCenter.latitude, binding.mapView.mapCenter.longitude)
            )
        )
        
        onDatabaseChangeListener(Device.getDeviceId(requireContext()), this)
        return binding.root
    }
    
    private fun setupLastSessionState() {
        osmdroidViewModel.getSessionState()
        
        osmdroidViewModel.sessionStateLiveData.observe(viewLifecycleOwner) { sessionState ->
            changeGridState(sessionState.isGrid)
            setMapType(sessionState.currentMap)
            binding.mapView.mapOrientation = sessionState.mapOrientation
            binding.azimuth.text = sessionState.azimuth
            binding.plane.text = sessionState.plane
            val mapController = binding.mapView.controller
            mapController.setZoom(sessionState.cameraZoomLevel)
            mapController.setCenter(GeoPoint(sessionState.latitude, sessionState.longitude))
        }
    }
    
    override fun setMapType(mapType: Int) {
        when (mapType) {
            MapType.OSM.value -> {
                binding.mapView.setTileSource(
                    MapTools.getOSMMapTile(
                        requireContext(),
                        binding.mapView
                    )
                )
                osmdroidViewModel.saveCurrentMapState(mapType)
                offlineOpenFileManager.deleteFileName()
            }
            MapType.SCHEME_MAP.value -> {
                binding.mapView.setTileSource(
                    MapTools.getGoogleMapTile(
                        requireContext(),
                        binding.mapView,
                        Pair("Google maps", "m")
                    )
                )
                osmdroidViewModel.saveCurrentMapState(mapType)
                offlineOpenFileManager.deleteFileName()
            }
            MapType.GOOGLE_HYB.value -> {
                binding.mapView.setTileSource(
                    MapTools.getGoogleMapTile(
                        requireContext(),
                        binding.mapView,
                        Pair("Google hybrid", "y")
                    )
                )
                osmdroidViewModel.saveCurrentMapState(mapType)
                offlineOpenFileManager.deleteFileName()
            }
            MapType.GOOGLE_SAT.value -> {
                binding.mapView.setTileSource(
                    MapTools.getGoogleMapTile(
                        requireContext(),
                        binding.mapView,
                        Pair("Google satellite", "s")
                    )
                )
                osmdroidViewModel.saveCurrentMapState(mapType)
                offlineOpenFileManager.deleteFileName()
            }
            MapType.OFFLINE.value -> {
                val offlineMapFileName = offlineOpenFileManager.getFileName()
                if (offlineMapFileName != null)
                    openFile(offlineMapFileName, binding.mapView, requireContext())
                else offlineMode(binding.mapView, requireContext())
                osmdroidViewModel.saveCurrentMapState(mapType)
            }
        }
    }
    
    override fun cacheMap() {
        if (binding.mapView.useDataConnection()) {
            val cacheManager = CacheManager(binding.mapView)
            cacheManager.verifyCancel = true
            cacheManager.downloadAreaAsync(requireContext(), binding.mapView.boundingBox, 1, 18)
        } else {
            Toast.makeText(
                requireContext(),
                "Скачивание невозможно в оффлайн-режиме",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun setupOsmdroidMap() = binding.run {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(3.0)
        binding.mapView.minZoomLevel = 3.0
        
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        
        rotationGestureOverlay = RotationGestureOverlay(mapView)
        mapView.overlays.add(rotationGestureOverlay)
        
        mapView.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                val cameraTarget = binding.mapView.mapCenter as GeoPoint
                showGeoInformation(binding, cameraTarget, droneMarker.position)
                
                setPolyline(polylineToCenter, listOf(droneMarker.position, cameraTarget))
                
                binding.distance.text = "${getDistance(droneMarker.position, cameraTarget)} km"
                return true
            }
            
            override fun onZoom(event: ZoomEvent?): Boolean {
                return true
            }
        })
        
        setupManipulators(binding, rotationGestureOverlay)
        
        checkLocationPermissions((activity as MainActivity))
        initMyLocation()
    }
    
    override fun initDroneMarker() {
        droneMarker = Marker(binding.mapView)
        drawMarker(
            droneMarker,
            Technic(
                coords = Coordinates(x = 0.0, y = 0.0),
                type = TechnicTypes.DRONE
            )
        )
        droneMarker.isFlat = true
        polylineToCenter = Polyline()
        polylineToAim.isVisible = false
    }
    
    private fun setPolyline(polyline: Polyline, points: List<GeoPoint>, color: Int = Color.BLUE) {
        polyline.setPoints(points)
        polyline.color = color
        polyline.width = 0.2f
        binding.mapView.overlays.add(polyline)
        polylineToAim.isVisible = true
    }
    
    override fun initTechnic() {
        osmdroidViewModel.getTechnics()
        osmdroidViewModel.technicListLiveData.observe(viewLifecycleOwner) {
            it?.let { list ->
                list.forEach { technic ->
                    val mark = Marker(binding.mapView)
                    drawMarker(mark, technic)
                    
                    listOfTechnic.add(mark)
                    addClickListenerToMark(mark, technic.type)
                }
            }
        }
    }
    
    override fun spawnTechnic(type: TechnicTypes, coords: Coordinates?) {
        if (coords != null)
            for (technic in listOfTechnic) {
                technic as Marker
                if (technic.position.latitude == coords.x &&
                    technic.position.longitude == coords.y
                ) return
            }
        
        val cameraPosition = binding.mapView.mapCenter
        osmdroidViewModel.getTechnics()
        var count = 0
        osmdroidViewModel.technicListLiveData.observe(this) { technicList ->
            count = technicList.size + 1
            val mark: Marker = if (coords != null)
                setMark(coords.x, coords.y, type)
            else
                setMark(cameraPosition.latitude, cameraPosition.longitude, type)
            
            addClickListenerToMark(mark, type)
            osmdroidViewModel.saveTechnic(
                Technic(
                    id = count,
                    type = type,
                    Coordinates(x = mark.position.latitude, y = mark.position.longitude)
                )
            )
        }
    }
    
    private fun addClickListenerToMark(mark: Marker, type: TechnicTypes) {
        val technic = Technic(
            coords = Coordinates(
                x = mark.position.latitude,
                y = mark.position.longitude
            ), type = type
        )
        
        mark.setOnMarkerClickListener { marker, mapView ->
            val targetFragment = TargetFragment(technic = technic,
                object : TargetFragment.TargetFragmentCallback {
                    override fun onBroadcastButtonClick(destinationId: String, technic: Technic) {
                        sendMessage(destinationId, technic)
                    }
                    
                    override fun deleteTarget() {
                        binding.mapView.overlays.remove(mark)
                        listOfTechnic.remove(mark)
                        osmdroidViewModel.deleteTechnic(technic)
                    }
                    
                }
            )
            targetFragment.show(parentFragmentManager, "targFragment")
            true
        }
        
    }
    
    private fun setMark(
        latitude: Double,
        longitude: Double,
        type: TechnicTypes
    ): Marker {
        aimMarker?.let {
            if (abs(latitude - it.position.latitude) < 0.0001
                && abs(longitude - it.position.longitude) < 0.0001
            )
                removeAim()
        }
        var mark = Marker(binding.mapView)
        mark = drawMarker(
            mark, Technic(
                coords = Coordinates(x = latitude, y = longitude),
                type = type
            )
        )
        listOfTechnic.add(mark)
        return mark
    }
    
    private fun drawMarker(mark: Marker?, technic: Technic): Marker {
        var marker = mark
        if (marker == null)
            marker = Marker(binding.mapView)
        marker.position = GeoPoint(technic.coords.x, technic.coords.y)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        binding.mapView.overlays.add(marker)
        marker.icon = getDrawable(requireContext(), ImageTypes.imageMap[technic.type]!!)
        binding.mapView.invalidate()
        
        return marker
    }
    
    override fun removeAim() {
        aimMarker?.remove(binding.mapView)
        aimMarker = null
        polylineToAim.isVisible = false
    }
    
    override fun showDataFromDrone(entities: List<Entity>) {
        droneMarker.rotation = -entities[0].asim.toFloat()
        if (entities[0].lat.isNaN() && entities[0].lon.isNaN()) {
            droneMarker.position = GeoPoint(0.0, 0.0)
        } else {
            droneMarker.position = GeoPoint(entities[0].lat, entities[0].lon)
        }
        val cameraTarget =
            GeoPoint(binding.mapView.mapCenter.latitude, binding.mapView.mapCenter.longitude)
        droneMarker.setVisible(true)
        showGeoInformation(binding, cameraTarget, droneMarker.position)
        setPolyline(polylineToCenter, listOf(droneMarker.position, cameraTarget))
        
        showAim(GeoPoint(entities[1].lat, entities[1].lon))
        
        if (entities[0].calc_target) {
            osmdroidViewModel.getTargetCoordinates(entities)
            osmdroidViewModel.targetLiveData.observe(this) { findTarget ->
                spawnTechnic(
                    TechnicTypes.ANOTHER,
                    Coordinates(x = findTarget.lat, y = findTarget.lon)
                )
            }
        }
    }
    
    private fun showAim(aim: GeoPoint) {
        if (aimMarker != null)
            removeAim()
        
        aimMarker = drawMarker(
            aimMarker,
            Technic(
                coords = Coordinates(x = aim.latitude, y = aim.longitude), type = TechnicTypes.AIM
            )
        )
        setPolyline(polylineToAim, listOf(droneMarker.position, aimMarker!!.position), Color.GREEN)
    }
    
    private fun focusCamera(point: GeoPoint) {
        binding.mapView.controller.animateTo(point)
        binding.mapView.controller.zoomIn()
    }
    
    override fun showLocationDialog() {
        showLocationDialog(requireContext(), object : LocationDialogCallback {
            override fun focusCamera() {
                if (aimMarker != null)
                    focusCamera(aimMarker!!.position)
                else
                    focusCamera(droneMarker.position)
            }
            
            override fun findMyLocation() {
                if (checkLocationPermissions((activity as MainActivity)))
                    if (checkGPS())
                        locationOverlay?.let {
                            if (it.myLocation != null) focusCamera(it.myLocation)
                        }
            }
        })
    }
    
    private fun checkGPS(): Boolean {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(600)
            .setMaxUpdateDelayMillis(1000)
            .build()
        
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()
        val res = LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(builder)
        
        res.addOnCompleteListener { task ->
            try {
                // when the GPS is on
                task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                // when the GPS is OFF
                e.printStackTrace()
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // here we send the request for enable the GPS
                        val resolveApiException = e as ResolvableApiException
                        resolveApiException.startResolutionForResult(requireActivity(), 200)
                    } catch (sendIntentException: IntentSender.SendIntentException) {
                        sendIntentException.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // when the setting is unavailable
                    }
                }
            }
        }
        res.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    // Handle result in onActivityResult()
                    e.startResolutionForResult(requireActivity(), 999)
                } catch (sendEx: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
            e.printStackTrace()
        }
        
        val locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        val isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        
        return isGPS
    }
    
    private fun initMyLocation() {
        val provider = GpsMyLocationProvider(requireContext())
        provider.addLocationSource(LocationManager.NETWORK_PROVIDER)
        locationOverlay = MyLocationNewOverlay(provider, binding.mapView)
        locationOverlay?.enableMyLocation()
        binding.mapView.overlayManager.add(locationOverlay)
    }
    
    override fun deleteAll() = binding.run {
        mapView.overlays.removeAll(listOfTechnic)
        osmdroidViewModel.deleteAll()
        listOfTechnic.clear()
        binding.mapView.invalidate()
        return@run
    }
    
    override fun offlineMode() {
        offlineMode(binding.mapView, requireContext())
    }
    
    override fun changeGridState(isGrid: Boolean) {
        if (isGrid) {
            binding.mapView.overlays.add(overlayGrid)
            osmdroidViewModel.saveGridState(isGrid)
        } else {
            binding.mapView.overlays.remove(overlayGrid)
            osmdroidViewModel.saveGridState(isGrid)
        }
        binding.mapView.invalidate()
    }
    
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        Configuration.getInstance().save(requireContext(), prefs)
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        locationOverlay?.enableMyLocation()
    }
    
    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        locationOverlay?.disableMyLocation()
    }
    
    override fun onDetach() {
        super.onDetach()
        binding.mapView.onDetach()
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        osmdroidViewModel.onSaveInstanceState(
            azimuth = binding.azimuth.text.toString(),
            plane = binding.plane.text.toString(),
            mapView = binding.mapView
        )
    }
}
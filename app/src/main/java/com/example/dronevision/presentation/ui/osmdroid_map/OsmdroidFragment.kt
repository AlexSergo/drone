package com.example.dronevision.presentation.ui.osmdroid_map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.util.Pair
import com.example.dronevision.databinding.FragmentOsmdroidBinding
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.delegates.LocationDialogCallback
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.model.bluetooth.Entity
import com.example.dronevision.presentation.ui.MyMapFragment
import com.example.dronevision.presentation.ui.targ.TargetFragment
import com.example.dronevision.utils.ImageTypes
import com.example.dronevision.utils.MapTools
import com.example.dronevision.utils.MapType
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2
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
    
        onDatabaseChangeListener(this)
        return binding.root
    }
    
    private fun setupLastSessionState() {
        osmdroidViewModel.getSessionState()
    
        osmdroidViewModel.sessionStateLiveData.observe(viewLifecycleOwner) { sessionState ->
            changeGridState(sessionState.isGrid)
            setMapType(sessionState.currentMap)
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
            }
            MapType.YANDEX_MAP.value -> {
                binding.mapView.setTileSource(
                    MapTools.getYandexMapTile(
                        requireContext(),
                        binding.mapView
                    )
                )
                osmdroidViewModel.saveCurrentMapState(mapType)
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
            }
            MapType.GOOGLE_SAT.value -> {
                binding.mapView.setTileSource(
                    MapTools.getGoogleMapTile(
                        requireContext(),
                        binding.mapView,
                        Pair("Google sattelite", "s")
                    )
                )
                osmdroidViewModel.saveCurrentMapState(mapType)
            }
            MapType.OFFLINE.value -> {
                offlineMode(binding.mapView, requireContext())
            }
        }
    }
    
    private fun setupOsmdroidMap() = binding.run {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(2.0)
        binding.mapView.minZoomLevel = 3.0
        
        val compassOverlay = CompassOverlay(requireContext().applicationContext, mapView)
        compassOverlay.enableCompass()
        mapView.overlays.add(compassOverlay)
        
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        
        rotationGestureOverlay = RotationGestureOverlay(mapView)
        mapView.overlays.add(rotationGestureOverlay)

        mapView.addMapListener(object : MapListener{
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
    }

    override fun initDroneMarker() {
        droneMarker = Marker(binding.mapView)
        drawMarker(droneMarker,
            Technic( coords = Coordinates(x = 0.0, y = 0.0),
                type = TechnicTypes.DRONE))
        droneMarker.isFlat = true
        polylineToCenter = Polyline()
        polylineToAim.isVisible = false
    }

    private fun setPolyline(polyline: Polyline, points: List<GeoPoint>, color: Int = Color.BLUE){
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
        val technic = Technic(coords = Coordinates(
                x = mark.position.latitude,
                y = mark.position.longitude
            ), type = type)

        mark.setOnMarkerClickListener { marker, mapView ->
            val targetFragment = TargetFragment( technic = technic,
                object : TargetFragment.TargetFragmentCallback{
                    override fun onBroadcastButtonClick(technic: Technic) {
                       sendMessage(technic)
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
            if ( abs(latitude - it.position.latitude) < 0.0001
                && abs(longitude - it.position.longitude) < 0.0001)
                removeAim()
        }
        var mark = Marker(binding.mapView)
        mark = drawMarker(mark, Technic(
            coords = Coordinates(x= latitude, y = longitude),
            type = type)
        )
        listOfTechnic.add(mark)
        return mark
    }

    private fun drawMarker(mark: Marker?,technic: Technic): Marker{
        var  marker = mark
        if (marker == null)
            marker = Marker(binding.mapView)
        marker.position = GeoPoint(technic.coords.x, technic.coords.y)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        binding.mapView.overlays.add(marker)
        marker.icon = getDrawable(requireContext(), ImageTypes.imageMap[technic.type]!!)
        binding.mapView.invalidate()

        return marker
    }

    override fun removeAim(){
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
        val cameraTarget = GeoPoint(binding.mapView.mapCenter.latitude, binding.mapView.mapCenter.longitude)
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

    private fun showAim(aim: GeoPoint){
        if (aimMarker != null)
            removeAim()

        aimMarker = drawMarker(aimMarker,
            Technic(coords = Coordinates(x = aim.latitude, y = aim.longitude),
                    type = TechnicTypes.AIM
            )
        )
        setPolyline(polylineToAim, listOf(droneMarker.position, aimMarker!!.position), Color.GREEN)
    }

    private fun focusCamera(point: GeoPoint){
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
        })
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
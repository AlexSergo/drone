package com.example.dronevision.presentation.ui.yandex_map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.dronevision.R
import com.example.dronevision.databinding.FragmentYandexMapBinding
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.*
import com.example.dronevision.presentation.ui.bluetooth.Entity
import com.example.dronevision.presentation.ui.targ.TargFragment
import com.example.dronevision.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider
import kotlin.math.abs
import kotlin.math.roundToInt


class YandexMapFragment :  MyMapFragment<PlacemarkMapObject>(), CameraListener,
TargFragment.TargetFragmentCallback, IMap{
    
    private lateinit var binding: FragmentYandexMapBinding
    private lateinit var droneMarker: PlacemarkMapObject
    private var aimMarker: PlacemarkMapObject? = null

    private lateinit var polylineONMap: PolylineMapObject
    private var polylineToAim: PolylineMapObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(this)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYandexMapBinding.inflate(inflater, container, false)
    
        binding.mapView.map.addCameraListener(this)

        initDroneMarker()
        initTechnic()
    
        setupCompassButton()
        setupZoomButtons()
        setCardViewExpand()

        onDatabaseChangeListener(databaseRef, this)
        
        return binding.root
    }
    
    private fun setCardViewExpand() {
        var isCardViewExpanded = true
        binding.infoCard.setOnClickListener {
            isCardViewExpanded = !isCardViewExpanded
            binding.plane.isVisible = isCardViewExpanded
            binding.longitude.isVisible = isCardViewExpanded
            binding.latitude.isVisible = isCardViewExpanded
            binding.azimuth.isVisible = isCardViewExpanded
        }
    }

    private fun setupCompassButton() {
        binding.compassButton.setOnClickListener {
            val cameraPosition = binding.mapView.map.cameraPosition
            binding.mapView.map.move(
                CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom, 0.0f, cameraPosition.tilt
                ), Animation(Animation.Type.SMOOTH, 1.0f), null
            )
        }
    }

    private fun setupZoomButtons() {
        binding.zoomInButton.setOnClickListener {
            val cameraPosition = binding.mapView.map.cameraPosition
            binding.mapView.map.move(
                CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom + 1, cameraPosition.azimuth, cameraPosition.tilt
                ), Animation(Animation.Type.SMOOTH, 1.0f), null
            )
        }

        binding.zoomOutButton.setOnClickListener {
            val cameraPosition = binding.mapView.map.cameraPosition
            binding.mapView.map.move(
                CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom - 1, cameraPosition.azimuth, cameraPosition.tilt
                ), Animation(Animation.Type.SMOOTH, 1.0f), null
            )
        }
    }

    override fun initTechnic() {
        viewModel.getTechnics()
        viewModel.technicListLiveData.observe(viewLifecycleOwner) {
            it?.let { list ->
                list.forEach { technic ->
                    val mark = drawMarker(technic)
                    listOfTechnic.add(mark)
                    addClickListenerToMark(mark, technic.type)
                }
            }
        }
    }

    private fun drawMarker(technic: Technic): PlacemarkMapObject{
        val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
        val mark = mapObjCollection.addPlacemark(
            Point(technic.coords.x, technic.coords.y),
            ImageProvider.fromResource(
                requireContext(),
                ImageTypes.imageMap[technic.type]!!
            )
        )
        return mark
    }

    override fun spawnTechnic(type: TechnicTypes, coords: Coordinates?) {
        if (coords != null)
            for (technic in listOfTechnic)
                if (technic.geometry.latitude == coords.x &&
                    technic.geometry.longitude == coords.y)
                    return

        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        viewModel.getTechnics()
        var count = 0
        viewModel.technicListLiveData.observe(viewLifecycleOwner) {
            it?.let {
                count = it.size + 1
                val mark: PlacemarkMapObject = if (coords != null)
                    setMark(coords.x, coords.y, type)
                else
                    setMark(cameraPositionTarget.latitude, cameraPositionTarget.longitude, type)

                addClickListenerToMark(mark, type)
                viewModel.saveTechnic(
                    Technic(
                        id = count,
                        type = type,
                        Coordinates(x = mark.geometry.latitude, y = mark.geometry.longitude)
                    )
                )
            }
        }
    }

    private fun setMark(
        latitude: Double,
        longitude: Double,
        type: TechnicTypes
    ): PlacemarkMapObject {
        aimMarker?.let {
            if ( abs(latitude - it.geometry.latitude) < 0.0001
                && abs(longitude - it.geometry.longitude) < 0.0001)
                removeAim()
        }
        val mark = drawMarker(Technic(
            coords = Coordinates(x = latitude, y = longitude),
            type = type))

        listOfTechnic.add(mark)
        return mark
    }


    override fun removeAim(){
        aimMarker?.parent?.remove(aimMarker!!)
        aimMarker = null
        polylineToAim?.parent?.remove(polylineToAim!!)
    }

    private fun addClickListenerToMark(mark: PlacemarkMapObject, type: TechnicTypes) {
        mark.addTapListener { mapObject, point ->
            Toast.makeText(
                requireContext(),
                "${mark.geometry.latitude} ${mark.geometry.longitude}",
                Toast.LENGTH_SHORT
            ).show()
            val targFragment = TargFragment(
                Technic(
                    coords = Coordinates(
                        x = mark.geometry.latitude,
                        y = mark.geometry.longitude
                    ), type = type
                ),
                this
            )
            targFragment.show(parentFragmentManager, "targFragment")
            true
        }
    }
    
    override fun onBroadcastButtonClick(technic: Technic) {
        val sb = StringBuilder()
        sb.append(technic.type.name)
        sb.append(" ")
        sb.append(technic.coords.x)
        sb.append(" ")
        sb.append(technic.coords.y)
        databaseRef.setValue(sb.toString())
    }

    private fun editDroneMarker(
        latitude: Double,
        longitude: Double,
        asim: Float,
    ): PlacemarkMapObject {
        droneMarker.direction = asim
        if (latitude.isNaN() && longitude.isNaN()) {
            droneMarker.geometry = Point(0.0, 0.0)
        } else {
            droneMarker.geometry = Point(latitude, longitude)
        }
        droneMarker.isVisible = true
        droneMarker.addTapListener { mapObject, point ->
            return@addTapListener true
        }

        showGeoInformation(binding,
            binding.mapView.map.cameraPosition.target, droneMarker.geometry)
        editPolylineOnMapGeometry()

        binding.mapView.alpha = 0f
        return droneMarker
    }

    private fun editPolylineOnMapGeometry() {
        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        polylineONMap.geometry = Polyline(listOf(droneMarker.geometry, cameraPositionTarget))
    }

    override fun showDataFromDrone(entities: List<Entity>) {
        val drone = entities[0]
        editDroneMarker(drone.lat, drone.lon, drone.asim.toFloat())

        val aim = entities[1]
        showAim(aim.lat, aim.lon)

        if (entities[0].calc_target) {
            targetViewModel.getTargetCoordinates(entities)
            targetViewModel.targetLiveData.observe(viewLifecycleOwner){
                spawnTechnic(
                    TechnicTypes.ANOTHER,
                    Coordinates(x = it.lat, y = it.lon)
                )
            }
        }
    }

    override fun deleteAll() {
        binding.mapView.map.mapObjects.clear()
        viewModel.deleteAll()
        listOfTechnic.clear()
        initDroneMarker()
    }
    
    override fun offlineMode() {
        Toast.makeText(requireContext(), "Яндекс карты не позволяют зайти в оффлайн мод", Toast.LENGTH_SHORT).show()
    }

    override fun changeGridState(isShow: Boolean) {
        Toast.makeText(requireContext(), "Яндекс карты не позволяют активировать сетку", Toast.LENGTH_SHORT).show()
    }

    private fun showAim(latitude: Double, longitude: Double) {
        if (aimMarker != null || polylineToAim != null)
            removeAim()

        aimMarker = binding.mapView.map.mapObjects.addPlacemark(
            Point(latitude, longitude),
            ImageProvider.fromResource(requireContext(), R.drawable.ic_cross_center)
        )
            //focusCamera(latitude, longitude)
        drawPolylineToAim(from = droneMarker.geometry, to = Point(latitude, longitude))
    }

    private fun drawPolylineToAim(from: Point, to: Point) {
        val polyline = Polyline(listOf(from, to))
        polylineToAim = binding.mapView.map.mapObjects.addPolyline(polyline)
        polylineONMap.strokeWidth = 0.2f
        polylineToAim?.outlineColor = Color.GREEN
    }

    private fun focusCamera(latitude: Double, longitude: Double) {
        val cameraPosition = binding.mapView.map.cameraPosition
        binding.mapView.map.move(
            CameraPosition(
                Point(latitude, longitude),
                cameraPosition.zoom + 1, 0f, 0f
            ),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    override fun initDroneMarker() {
        droneMarker = binding.mapView.map.mapObjects.addPlacemark(
            Point(0.0, 0.0),
            ImageProvider.fromResource(requireContext(), R.drawable.gps_tacker2)
        )
        droneMarker.setIconStyle(IconStyle().setRotationType(RotationType.ROTATE))
        droneMarker.isVisible = false

        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        val latitude = cameraPositionTarget.latitude
        val longitude = cameraPositionTarget.longitude

        val polyline = Polyline(listOf(Point(latitude, longitude), Point(0.0, 0.0)))
        polylineONMap = binding.mapView.map.mapObjects.addPolyline(polyline)
        polylineONMap.strokeWidth = 0.2f
    }

    override fun showLocationDialog() {
        val locationDialogArray = arrayOf(
            "Запросить у Р-187-П1",
            "Запросить у Android",
            "Снять с карты",
            "Найти на карте",
            "Передать Р-187-П1"
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Координаты")
            .setItems(locationDialogArray) { dialog, which ->
                when (which) {
                    0 -> {}
                    1 -> {}
                    2 -> {}
                    3 -> {
                        if (aimMarker != null)
                            aimMarker?.geometry?.let { focusCamera(it.latitude, aimMarker!!.geometry.longitude) }
                        else
                            getMarkerLocation()
                    }
                    4 -> {}
                }
            }
            .show()
    }

    private fun getMarkerLocation() {
        binding.mapView.map.move(
            CameraPosition(
                Point(droneMarker.geometry.latitude, droneMarker.geometry.longitude),
                12.0f,
                0.0f,
                0.0f
            ),
            Animation(Animation.Type.SMOOTH, 1.0f), null
        )
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {

        showGeoInformation(binding, cameraPosition.target, droneMarker.geometry)

        binding.compassButton.rotation = cameraPosition.azimuth * -1

        polylineONMap.geometry = Polyline(listOf(droneMarker.geometry, cameraPosition.target))

        val distance = (Geo.distance(droneMarker.geometry, cameraPosition.target) / 100).roundToInt() / 10.0
        binding.distance.text = "$distance km"
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}
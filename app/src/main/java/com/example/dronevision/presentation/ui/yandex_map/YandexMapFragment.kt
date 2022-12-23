package com.example.dronevision.presentation.ui.yandex_map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dronevision.App
import com.example.dronevision.R
import com.example.dronevision.databinding.FragmentYandexMapBinding
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.IMap
import com.example.dronevision.presentation.ui.ImageTypes
import com.example.dronevision.presentation.ui.bluetooth.Entity
import com.example.dronevision.presentation.ui.targ.TargFragment
import com.example.dronevision.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.math.abs


class YandexMapFragment : Fragment(), CameraListener,
TargFragment.TargetFragmentCallback, IMap {
    
    private lateinit var binding: FragmentYandexMapBinding
    private lateinit var droneMarker: PlacemarkMapObject
    private var aimMarker: PlacemarkMapObject? = null

    private lateinit var polylineONMap: PolylineMapObject
    private var polylineToAim: PolylineMapObject? = null
    
    private lateinit var viewModel: YandexMapViewModel
    private lateinit var databaseRef: DatabaseReference
    
    private val listOfObjects = mutableListOf<PlacemarkMapObject>()
    
    @Inject
    lateinit var viewModelFactory: YandexMapViewModelFactory
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        initViewModel()
    }
    
    private fun inject() {
        (requireContext().applicationContext as App).appComponent.inject(this)
    }
    
    private fun initViewModel() {
        viewModel = ViewModelProvider(this, viewModelFactory)[YandexMapViewModel::class.java]
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYandexMapBinding.inflate(inflater, container, false)
    
        binding.mapView.map.addCameraListener(this)

        initDroneMarker()
        initTechnic()
    
        SpawnTechnic.spawnTechnicLiveData.observe(viewLifecycleOwner) {
            spawnTechnic(it.imageRes, it.type)
        }
    
        setupCompassButton()
        setupZoomButtons()
        setCardViewExpand()
        
        val database =
            Firebase.database("https://drone-6c66c-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseRef = database.getReference("message")
        onDatabaseChangeListener(databaseRef)
        
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

    private fun initTechnic() {
        viewModel.getTechnics()
        viewModel.technicListLiveData.observe(viewLifecycleOwner) {
            it?.let { list ->
                list.forEach { technic ->
                    val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                    val mark = mapObjCollection.addPlacemark(
                        Point(technic.coords.x, technic.coords.y),
                        ImageProvider.fromResource(
                            requireContext(),
                            ImageTypes.imageMap[technic.type]!!
                        )
                    )
                    mark.geometry.latitude
                    mark.geometry.longitude
                    listOfObjects.add(mark)
                    addClickListenerToMark(mark, technic.type)
                }
            }
        }
    }

    private fun onDatabaseChangeListener(dRef: DatabaseReference) {
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (null == snapshot.value) return
                val str = snapshot.value.toString().split(" ")
                val lat = str[1].toDouble()
                val lon = str[2].toDouble()
                for (mark in listOfObjects)
                    if (mark.geometry.longitude == lon && mark.geometry.latitude == lat)
                        return

                spawnTechnic(
                    ImageTypes.imageMap[TechnicTypes.valueOf(str[0])]!!,
                    TechnicTypes.valueOf(str[0]),
                    Coordinates(x = str[1].toDouble(), y = str[2].toDouble())
                )
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun spawnTechnic(
        @DrawableRes imageRes: Int,
        type: TechnicTypes,
        coords: Coordinates? = null
    ) {
        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        viewModel.getTechnics()
        var count = 0
        viewModel.technicListLiveData.observe(viewLifecycleOwner) {
            it?.let {
                count = it.size + 1
                val mark: PlacemarkMapObject = if (coords != null)
                    setMark(coords.x, coords.y, imageRes)
                else
                    setMark(cameraPositionTarget.latitude, cameraPositionTarget.longitude, imageRes)

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
        @DrawableRes imageRes: Int
    ): PlacemarkMapObject {
        val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
        aimMarker?.let {
            if ( abs(latitude - it.geometry.latitude) < 0.0001
                && abs(longitude - it.geometry.longitude) < 0.0001)
                removeAim()
        }
        val mark: PlacemarkMapObject = mapObjCollection.addPlacemark(
            Point(latitude, longitude),
            ImageProvider.fromResource(requireContext(), imageRes)
        )
        mark.geometry.latitude
        mark.geometry.longitude
        listOfObjects.add(mark)
        return mark
    }


    private fun removeAim(){
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

        showWgs82OnCard()
        calculateAzimuth()
        showSk42OnCard()
        editPolylineOnMapGeometry()
        return droneMarker
    }

    private fun editPolylineOnMapGeometry() {
        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        polylineONMap.geometry = Polyline(listOf(droneMarker.geometry, cameraPositionTarget))
    }

    override fun showLocationFromDrone(entities: List<Entity>) {
        val drone = entities[0]
        editDroneMarker(drone.lat, drone.lon, drone.asim.toFloat())
        val aim = entities[1]
        showAim(aim.lat, aim.lon)
        if (entities[0].calc_target) {
            getTargetCoordinates(entities)
            CalculateTargetCoordinates.targetLiveData.observe(viewLifecycleOwner) {
                spawnTechnic(
                    R.drawable.ic_99,
                    TechnicTypes.ANOTHER,
                    Coordinates(x = it.lat, y = it.lon)
                )
            }
        }
    }

    override fun deleteAll() {
        binding.mapView.map.mapObjects.clear()
        viewModel.deleteAll()
        listOfObjects.clear()
        initDroneMarker()
    }
    
    override fun offlineMode() {
        Toast.makeText(requireContext(), "Яндекс карты не позволяют зайти в оффлайн мод", Toast.LENGTH_SHORT).show()
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

    private fun initDroneMarker() {
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
                            focusCamera(aimMarker!!.geometry.latitude, aimMarker!!.geometry.longitude)
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

    private fun getTargetCoordinates(entities: List<Entity>) = lifecycleScope.launch {
        val drone = entities[0]
        var lat = drone.lat
        var lon = drone.lon
        if (lat.isNaN() && lon.isNaN()) {
            lat = 0.0
            lon = 0.0
        }
        val alt = drone.alt
        val ywr = drone.cam_deflect
        val pt = drone.cam_angle
        val asim = drone.asim
        var findTarget = FindTarget(alt, lat, lon, asim + ywr, pt)
        CalculateTargetCoordinates.targetLiveData.postValue(findTarget)
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        showWgs82OnCard()
        calculateAzimuth()
        showSk42OnCard()
        binding.compassButton.rotation = cameraPosition.azimuth * -1

        polylineONMap.geometry = Polyline(listOf(droneMarker.geometry, cameraPosition.target))
    }
    
    private fun getCameraPositionLatitude(): Double =
        binding.mapView.map.cameraPosition.target.latitude
    
    private fun getCameraPositionLongitude(): Double =
        binding.mapView.map.cameraPosition.target.longitude
    
    private fun showWgs82OnCard() {
        val latitudeText = String.format("%.6f", getCameraPositionLatitude())
        val longitudeText = String.format("%.6f", getCameraPositionLongitude())
        binding.latitude.text = "Широта = $latitudeText"
        binding.longitude.text = "Долгота = $longitudeText"
    }
    
    private fun showSk42OnCard() {
        val x = doubleArrayOf(0.0)
        val y = doubleArrayOf(0.0)
        
        NGeoCalc().wgs84ToPlane(
            x, y,
            doubleArrayOf(0.0),
            NGeoCalc.degreesToRadians(getCameraPositionLatitude()),
            NGeoCalc.degreesToRadians(getCameraPositionLongitude()),
            0.0
        )
        binding.plane.text = String.format(
            "X= %d  Y= %08d", *arrayOf<Any>(
                Integer.valueOf(x[0].toInt()), Integer.valueOf(y[0].toInt())
            )
        )
    }
    
    private fun calculateAzimuth() {
        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        val azimuth = MapTools.angleBetween(droneMarker.geometry, cameraPositionTarget)
        val azimuthText = String.format("%.6f", azimuth)
        binding.azimuth.text = "Азимут = $azimuthText"
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

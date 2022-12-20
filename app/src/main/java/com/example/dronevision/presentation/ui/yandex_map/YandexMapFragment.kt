package com.example.dronevision.presentation.ui.yandex_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.example.dronevision.utils.MapTools
import com.example.dronevision.utils.SpawnTechnic
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
import javax.inject.Inject

class YandexMapFragment : Fragment(), CameraListener,
TargFragment.TargetFragmentCallback, IMap {
    
    private lateinit var binding: FragmentYandexMapBinding
    private lateinit var droneMarker: PlacemarkMapObject
    
    private lateinit var polylineONMap: PolylineMapObject
    
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
        
        setupZoomButtons()
    
    
        val database =
            Firebase.database("https://drone-6c66c-default-rtdb.asia-southeast1.firebasedatabase.app")
        databaseRef = database.getReference("message")
        onChangeListener(databaseRef)
        
        return binding.root
    }
    
    private fun setupZoomButtons() {
        binding.zoomInButton.setOnClickListener {
            val cameraPosition = binding.mapView.map.cameraPosition
            binding.mapView.map.move(
                CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom + 1, 0.0f, 0.0f
                ), Animation(Animation.Type.SMOOTH, 1.0f), null
            )
        }
        
        binding.zoomOutButton.setOnClickListener {
            val cameraPosition = binding.mapView.map.cameraPosition
            binding.mapView.map.move(
                CameraPosition(
                    cameraPosition.target,
                    cameraPosition.zoom - 1, 0.0f, 0.0f
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

    private fun onChangeListener(dRef: DatabaseReference){
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (null == snapshot.value) return
                val str = snapshot.value.toString().split(" ")
                val lat = str[1].toDouble()
                val lon = str[2].toDouble()
                val objects = binding.mapView.map.mapObjects
                for (mark in listOfObjects)
                    if (mark.geometry.longitude == lon && mark.geometry.latitude == lat)
                        return

                spawnTechnic(ImageTypes.imageMap[TechnicTypes.valueOf(str[0])]!!,
                    TechnicTypes.valueOf(str[0]), Coordinates(x = str[1].toDouble(), y = str[2].toDouble())
                )
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun spawnTechnic(@DrawableRes imageRes: Int, type: TechnicTypes, coords: Coordinates? = null) {
        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
        var mark: PlacemarkMapObject
        if (coords != null)
            mark = mapObjCollection.addPlacemark(
                Point(coords.x, coords.y),
                ImageProvider.fromResource(requireContext(), imageRes)
            )
        else
            mark = mapObjCollection.addPlacemark(
                Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
                ImageProvider.fromResource(requireContext(), imageRes)
            )

        mark.geometry.latitude
        mark.geometry.longitude
        listOfObjects.add(mark)
        addClickListenerToMark(mark, type)
        viewModel.getTechnics()
        var count = 0
        viewModel.technicListLiveData.observe(viewLifecycleOwner) {
            it?.let {
                count = it.size + 1
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
    
    private fun addClickListenerToMark(mark: PlacemarkMapObject, type: TechnicTypes) {
        mark.addTapListener { mapObject, point ->
            Toast.makeText(
                requireContext(),
                "${point.latitude} ${point.longitude}",
                Toast.LENGTH_SHORT
            ).show()
            val targFragment = TargFragment(
                Technic(coords = Coordinates(x = point.latitude, y = point.longitude), type = type),
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
        droneMarker.geometry = Point(latitude, longitude)
        droneMarker.isVisible = true
        droneMarker.addTapListener { mapObject, point ->
            return@addTapListener true
        }
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
    }
    
    override fun deleteAll() {
        binding.mapView.map.mapObjects.clear()
        viewModel.deleteAll()
        listOfObjects.clear()
        initDroneMarker()
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
        val latitude = cameraPosition.target.latitude
        val longitude = cameraPosition.target.longitude
        val latitudeText = String.format("%.6f", latitude)
        val longitudeText = String.format("%.6f", longitude)
        binding.latitude.text = "Широта = $latitudeText"
        binding.longitude.text = "Долгота = $longitudeText"
    
    
        polylineONMap.geometry = Polyline(listOf(droneMarker.geometry, cameraPosition.target))
    
        val azimuth = MapTools.angleBetween(droneMarker.geometry, cameraPosition.target)
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
package com.example.dronevision.presentation.ui.yandex_map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.dronevision.R
import com.example.dronevision.databinding.FragmentYandexMapBinding
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.delegates.LocationDialogCallback
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.IMap
import com.example.dronevision.presentation.ui.MyMapFragment
import com.example.dronevision.presentation.ui.bluetooth.Entity
import com.example.dronevision.presentation.ui.targ.TargFragment
import com.example.dronevision.utils.ImageTypes
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider
import kotlin.math.abs


class YandexMapFragment : MyMapFragment(), CameraListener, IMap {
    
    private lateinit var binding: FragmentYandexMapBinding
    private lateinit var droneMarker: PlacemarkMapObject
    private var aimMarker: PlacemarkMapObject? = null
    private val listOfTechnic = mutableListOf<PlacemarkMapObject>()
    
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
    
        setupManipulators(binding)
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

    override fun initTechnic() {
        yandexMapViewModel.getTechnics()
        yandexMapViewModel.technicListLiveData.observe(viewLifecycleOwner) {
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
                    technic.geometry.longitude == coords.y
                )
                    return
    
        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        yandexMapViewModel.getTechnics()
        var count = 0
        yandexMapViewModel.technicListLiveData.observe(this) { technicList ->
            count = technicList.size + 1
            val mark: PlacemarkMapObject = if (coords != null)
                setMark(coords.x, coords.y, type)
            else
                setMark(cameraPositionTarget.latitude, cameraPositionTarget.longitude, type)
    
            addClickListenerToMark(mark, type)
            yandexMapViewModel.saveTechnic(
                Technic(
                    id = count,
                    type = type,
                    Coordinates(x = mark.geometry.latitude, y = mark.geometry.longitude)
                )
            )
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
        val technic = Technic(coords = Coordinates(
            x = mark.geometry.latitude,
            y = mark.geometry.longitude
        ), type = type)

        mark.addTapListener { mapObject, point ->
            val targFragment = TargFragment(
                Technic(
                    coords = Coordinates(
                        x = mark.geometry.latitude,
                        y = mark.geometry.longitude
                    ), type = type
                ),
                object: TargFragment.TargetFragmentCallback{
                    override fun onBroadcastButtonClick(technic: Technic) {
                        val sb = StringBuilder()
                        sb.append(technic.type.name)
                        sb.append(" ")
                        sb.append(technic.coords.x)
                        sb.append(" ")
                        sb.append(technic.coords.y)
                        databaseRef.setValue(sb.toString())
                    }

                    override fun deleteTarget() {
                        binding.mapView.map.mapObjects.remove(mark)
                        listOfTechnic.remove(mark)
                        yandexMapViewModel.deleteTechnic(technic)
                    }

                }
            )
            targFragment.show(parentFragmentManager, "targFragment")
            true
        }
    }

    private fun editDroneMarkerPosition(
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

        showGeoInformation(binding,
            binding.mapView.map.cameraPosition.target, droneMarker.geometry)
        editPolylineOnMapGeometry()

        return droneMarker
    }

    private fun editPolylineOnMapGeometry() {
        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        polylineONMap.geometry = Polyline(listOf(droneMarker.geometry, cameraPositionTarget))
    }

    override fun showDataFromDrone(entities: List<Entity>) {
        val drone = entities[0]
        editDroneMarkerPosition(drone.lat, drone.lon, drone.asim.toFloat())

        val aim = entities[1]
        showAim(aim.lat, aim.lon)

        if (entities[0].calc_target) {
            yandexMapViewModel.getTargetCoordinates(entities)
            yandexMapViewModel.targetLiveData.observe(this, Observer {
                spawnTechnic(
                    TechnicTypes.ANOTHER,
                    Coordinates(x = it.lat, y = it.lon)
                )
            })
        }
    }

    override fun deleteAll() {
        aimMarker = null
        polylineToAim = null
        binding.mapView.map.mapObjects.clear()
        yandexMapViewModel.deleteAll()
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
        if (aimMarker != null)
            removeAim()

        aimMarker = binding.mapView.map.mapObjects.addPlacemark(
            Point(latitude, longitude),
            ImageProvider.fromResource(requireContext(), com.example.dronevision.R.drawable.ic_cross_center)
        )
        drawPolylineToAim(from = droneMarker.geometry, to = Point(latitude, longitude))
    }

    private fun drawPolylineToAim(from: Point, to: Point) {
        val polyline = Polyline(listOf(from, to))
        polylineToAim = binding.mapView.map.mapObjects.addPolyline(polyline)
        polylineONMap.strokeWidth = 0.2f
        polylineONMap.setStrokeColor(R.color.green)
        polylineToAim?.outlineColor = Color.GREEN
    }

    private fun focusCamera(point: Point) {
        val cameraPosition = binding.mapView.map.cameraPosition
        binding.mapView.map.move(
            CameraPosition(point,
                cameraPosition.zoom + 1, 0f, 0f
            ),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    override fun initDroneMarker() {
        droneMarker = binding.mapView.map.mapObjects.addPlacemark(
            Point(0.0, 0.0),
            ImageProvider.fromResource(requireContext(), com.example.dronevision.R.drawable.gps_tacker2)
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
    
    override fun setMapType(mapType: Int) {
        val action = YandexMapFragmentDirections.actionYandexMapFragmentToOsmdroidFragment()
        findNavController().navigate(action)
    }
    
    override fun showLocationDialog() {
        showLocationDialog(requireContext(), object : LocationDialogCallback {
            override fun focusCamera() {
                if (aimMarker != null)
                    focusCamera(aimMarker!!.geometry)
                else
                    focusCamera(droneMarker.geometry)
            }
        })
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
        binding.distance.text = "${getDistance(droneMarker.geometry, cameraPosition.target)} km"
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
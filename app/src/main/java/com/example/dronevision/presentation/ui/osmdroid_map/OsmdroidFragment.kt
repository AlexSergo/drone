package com.example.dronevision.presentation.ui.osmdroid_map

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.util.Pair
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.App
import com.example.dronevision.R
import com.example.dronevision.data.source.local.prefs.OfflineOpenFileManager
import com.example.dronevision.databinding.FragmentOsmdroidBinding
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.delegates.*
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.model.bluetooth.Entity
import com.example.dronevision.presentation.ui.MainActivity
import com.example.dronevision.presentation.ui.find_location.FindGeoPointCallback
import com.example.dronevision.presentation.ui.find_location.FindGeoPointFragment
import com.example.dronevision.presentation.ui.targ.TargetFragment
import com.example.dronevision.presentation.ui.targ.TargetFragmentCallback
import com.example.dronevision.utils.*
import com.example.dronevision.utils.AESEncyption.decrypt
import com.example.dronevision.utils.Device.toTechnic
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.RoundingMode
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.*


class OsmdroidFragment : Fragment(), IMap, RemoteDatabaseHandler by RemoteDatabaseHandlerImpl(),
    OfflineMapHandler by OfflineMapHandlerImpl(),
    GeoInformationHandler by GeoInformationHandlerImpl(),
    LocationDialogHandler by LocationDialogHandlerImpl(),
    ManipulatorSetuper by ManipulatorSetuperImpl(), GpsHandler by GpsHandlerImpl(),
    MapCachingHandler by MapCachingHandlerImpl() {

    private lateinit var binding: FragmentOsmdroidBinding
    private lateinit var rotationGestureOverlay: RotationGestureOverlay

    private val overlayGrid = LatLonGridlineOverlay2()
    private lateinit var droneMarker: Marker
    private lateinit var frontSightMarker: Marker
    private lateinit var aimMarker: Marker
    private lateinit var disruptionMarker: Marker
    private lateinit var exactTarget: Marker
    private var polylineToCenter: Polyline = Polyline()
    private var polylineToFrontSight: Polyline = Polyline()
    private val listOfTechnic = mutableListOf<Overlay>()
    private var locationOverlay: MyLocationNewOverlay? = null
    private var correctionAngRad: Double = 0.0
    private var correctionPolyline = Polyline()
    private lateinit var pointCalibration: PointCalibration

    private val getData = GetData()

    lateinit var osmdroidViewModel: OsmdroidViewModel

    @Inject
    lateinit var osmdroidViewModelFactory: OsmdroidViewModelFactory

    @Inject
    lateinit var offlineOpenFileManager: OfflineOpenFileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(this)
        initViewModel()
        pointCalibration = PointCalibration()
    }

    private fun inject(fragment: OsmdroidFragment) {
        (requireContext().applicationContext as App).appComponent.inject(fragment)
    }

    private fun initViewModel() {
        osmdroidViewModel =
            ViewModelProvider(this, osmdroidViewModelFactory)[OsmdroidViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOsmdroidBinding.inflate(inflater, container, false)
        setupOsmdroidMap()
        setupMarkers()
        setupPolylines()
        initTechnic()
        setupLastSessionState()
        setupDisruptionButtons()
        setupCorrectionButton()

        getData.updater()

        onDatabaseChangeListener(Device.getDeviceId(requireContext()),
            object : RemoteDatabaseCallback {
                override fun returnMessage(str: String) {
                    val s = str.substring(6, str.length - 1)
                    val decryptMessage = decrypt(s)
                    decryptMessage?.let {
                        val technic = it.toTechnic()
                        technic.division?.let {
                            spawnTechnic(
                                type = technic.technicTypes,
                                coords = Coordinates(technic.coordinates.x, technic.coordinates.y),
                                division = technic.division
                            )
                        }
                    }
                }
            })
        return binding.root
    }

    private fun getElevation(lat: Double, lon: Double): Short {
        PermissionTools.checkAndRequestPermissions(activity as MainActivity)

        val inputStream = requireContext().assets.open("N55E037.hgt")
        val file = File.createTempFile("temp", "N55E037.hgt")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        val fileChannel = FileInputStream(file).channel
        val buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())
        buffer.order(ByteOrder.BIG_ENDIAN)

        val latInt = lat.toInt()
        val lonInt = lon.toInt()
        val latIndex = 1 - (lat - latInt)
        val lonIndex = (lon - lonInt)
        val row = 3600 * latIndex
        val col = 3600 * lonIndex
        val index = round((round(row) * 3601.0 + round(col)) * 2).toInt()
        Log.d("lat", lat.toString())
        Log.d("lon", lon.toString())
        Log.d("row", row.toString())
        Log.d("col", col.toString())
        Log.d("index", index.toString())
        return if (index % 2 == 0) {
            val elevation = buffer.getShort(index)
            Log.d("elevation", elevation.toString())
            buffer.clear()
            elevation
        } else {
            val elevation = buffer.getShort(index + 1)
            Log.d("elevation", elevation.toString())
            buffer.clear()
            elevation
        }
    }

    private fun roundToFourDecimalPlaces(value: Double): Double {
        val decimalFormat = DecimalFormat("#.####")
        decimalFormat.roundingMode = RoundingMode.HALF_UP
        return decimalFormat.format(value).replace(",", ".").toDouble()
    }

    private fun setupMarkers() {
        aimMarker = Marker(binding.mapView)
        disruptionMarker = Marker(binding.mapView)
        frontSightMarker = Marker(binding.mapView)
        droneMarker = Marker(binding.mapView)
        exactTarget = Marker(binding.mapView)
        exactTarget.setVisible(false)
        droneMarker.isFlat = true

        drawMarker(
            exactTarget, Technic(
                coordinates = Coordinates(x = 32.0, y = 0.0), technicTypes = TechnicTypes.ANOTHER
            )
        )
        drawMarker(
            droneMarker, Technic(
                coordinates = Coordinates(x = 32.0, y = 0.0), technicTypes = TechnicTypes.DRONE
            )
        )
        drawMarker(
            aimMarker, Technic(
                coordinates = Coordinates(8.0, 8.0), technicTypes = TechnicTypes.AIM
            )
        )
        drawMarker(
            disruptionMarker, Technic(
                coordinates = Coordinates(4.0, 4.0), technicTypes = TechnicTypes.DISRUPTION
            )
        )
        drawMarker(
            frontSightMarker, Technic(
                coordinates = Coordinates(34.5, 2.5), technicTypes = TechnicTypes.FRONT_SIGHT
            )
        )
    }

    private fun setupCorrectionButton() {
        binding.correctionButton.setOnClickListener {
            if (correctionAngRad != 0.0) {
                correctionAngRad = 0.0
                it.background.setTint(resources.getColor(R.color.red))
                return@setOnClickListener
            }
            it.background.setTint(resources.getColor(R.color.teal_700))
            correctionPolyline = Polyline()
            correctionPolyline.setPoints(polylineToCenter.points)
            findCorrectionAngle()
        }
    }

    private fun setupDisruptionButtons() {
        var isAimVisible = false
        var isDisruptionVisible = false
        aimMarker.setVisible(isAimVisible)
        disruptionMarker.setVisible(isDisruptionVisible)

        binding.aimButton.setOnClickListener {
            if (isAimVisible) {
                isAimVisible = false
                aimMarker.setVisible(isAimVisible)
                binding.aimButton.background = getDrawable(requireContext(), R.color.white)
                binding.mapView.invalidate()
            } else {
                isAimVisible = true
                aimMarker.setVisible(isAimVisible)
                aimMarker.position = binding.mapView.mapCenter as GeoPoint
                binding.aimButton.background = getDrawable(requireContext(), R.color.green)
                binding.mapView.invalidate()
            }
            showDisruptionInf(isAimVisible, isDisruptionVisible)
        }

        binding.disruptionButton.setOnClickListener {
            if (isDisruptionVisible) {
                isDisruptionVisible = false
                disruptionMarker.setVisible(isDisruptionVisible)
                binding.disruptionButton.background = getDrawable(requireContext(), R.color.white)
                binding.mapView.invalidate()
            } else {
                isDisruptionVisible = true
                disruptionMarker.setVisible(isDisruptionVisible)
                disruptionMarker.position = binding.mapView.mapCenter as GeoPoint
                binding.disruptionButton.background = getDrawable(requireContext(), R.color.green)
                binding.mapView.invalidate()
            }
            showDisruptionInf(isAimVisible, isDisruptionVisible)
        }

        binding.sightingCard.setOnClickListener {
            val clipboard: ClipboardManager =
                requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val horizontalSighting = binding.sightingHorizontal.text.toString()
            val verticalSighting = binding.sightingVertical.text.toString()
            val copiedText = "$horizontalSighting, $verticalSighting"
            val clip = ClipData.newPlainText("sighting", copiedText)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(
                requireContext(),
                "?????????????????????? ${clipboard.primaryClip?.getItemAt(0)?.text.toString()}",
                Toast.LENGTH_LONG
            ).show()
        }

        binding.resetButton.visibility = View.INVISIBLE
        binding.resetButton.setOnClickListener {
            pointCalibration.reset()
            binding.resetButton.visibility = View.INVISIBLE
            exactTarget.setVisible(false)
        }

        binding.intersectionButton.visibility = View.VISIBLE
        binding.intersectionButton.setOnClickListener{
            pointCalibration.start()
            while (pointCalibration.isAlive)
            {}

            val res = pointCalibration.resultPoint
            if (res != null) {
                binding.resetButton.visibility = View.VISIBLE
                val divisionHandler = requireActivity() as DivisionHandler
                if (divisionHandler.checkDivision(requireContext())) {
                    val division = divisionHandler.getDivision(requireContext())
                    exactTarget.setVisible(true)
                    exactTarget.position = res
                    val technic = Technic(
                        coordinates = Coordinates(
                            x = exactTarget.position.latitude, y = exactTarget.position.longitude
                        ), technicTypes = TechnicTypes.ANOTHER, division = division
                    )
                    addClickListenerToMark(exactTarget, technic)
                }
                binding.intersectionButton.visibility = View.INVISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDisruptionInf(isAimVisible: Boolean, isDisruptionVisible: Boolean) {
        if (isAimVisible && isDisruptionVisible) {
            val xOfAim = doubleArrayOf(0.0)
            val yOfAim = doubleArrayOf(0.0)
            val xOfDisruption = doubleArrayOf(0.0)
            val yOfDisruption = doubleArrayOf(0.0)

            NGeoCalc().wgs84ToPlane(
                xOfAim,
                yOfAim,
                doubleArrayOf(0.0),
                NGeoCalc.degreesToRadians(aimMarker.position.latitude),
                NGeoCalc.degreesToRadians(aimMarker.position.longitude),
                0.0
            )

            NGeoCalc().wgs84ToPlane(
                xOfDisruption,
                yOfDisruption,
                doubleArrayOf(0.0),
                NGeoCalc.degreesToRadians(disruptionMarker.position.latitude),
                NGeoCalc.degreesToRadians(disruptionMarker.position.longitude),
                0.0
            )

            val xOfAimResult = Integer.valueOf(xOfAim[0].toInt())
            val yOfAimResult = Integer.valueOf(yOfAim[0].toInt())

            val xOfDisruptionResult = Integer.valueOf(xOfDisruption[0].toInt())
            val yOfDisruptionResult = Integer.valueOf(yOfDisruption[0].toInt())

            val horizontal = abs(xOfAimResult) - abs(xOfDisruptionResult)
            val vertical = abs(yOfAimResult) - abs(yOfDisruptionResult)

            if (horizontal > 0) binding.sightingHorizontal.text = "???? ${abs(horizontal)}m"
            else binding.sightingHorizontal.text = "?????????? ${abs(horizontal)}m"

            if (vertical > 0) binding.sightingVertical.text = "?????????? ${abs(vertical)}m"
            else binding.sightingVertical.text = "???????????? ${abs(vertical)}m"

            binding.sightingCard.isVisible = true
        } else binding.sightingCard.isGone = true
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
    
    override fun cacheMap() {
        cacheMap(binding.mapView, requireContext())
    }
    
    private fun setupOsmdroidMap() = binding.run {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(3.0)
        binding.mapView.minZoomLevel = 3.0
        binding.mapView.maxZoomLevel = 20.0
        
        mapView.setMultiTouchControls(true)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        
        rotationGestureOverlay = RotationGestureOverlay(mapView)
        mapView.overlays.add(rotationGestureOverlay)
        
        mapView.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                val cameraTarget = binding.mapView.mapCenter as GeoPoint
                showGeoInformation(binding, cameraTarget, droneMarker.position)
                updatePolyline(polylineToCenter, listOf(droneMarker.position, cameraTarget))
                binding.distance.text = "${getDistance(droneMarker.position, cameraTarget)} km"
                return true
            }
            
            override fun onZoom(event: ZoomEvent?): Boolean {
                return true
            }
        })
        
        setupManipulators(binding, rotationGestureOverlay)
        setupDisplayMetrics()
        initMyLocation()
    }
    
    private fun setupDisplayMetrics() {
        val displayMetrics = resources.displayMetrics
        val scaleBarOverlay = ScaleBarOverlay(binding.mapView)
        scaleBarOverlay.unitsOfMeasure = ScaleBarOverlay.UnitsOfMeasure.metric
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setTextSize(30.0f)
        scaleBarOverlay.setScaleBarOffset(
            displayMetrics.widthPixels / 2,
            displayMetrics.heightPixels - (displayMetrics.density * 110.0f).toInt()
        )
        binding.mapView.overlayManager.add(scaleBarOverlay)
    }
    
    override fun setMapType(mapType: Int) {
        when (mapType) {
            MapType.OSM.value -> {
                binding.mapView.setTileSource(
                    MapTools.getOSMMapTile(
                        requireContext(), binding.mapView
                    )
                )
                osmdroidViewModel.saveCurrentMapState(mapType)
                offlineOpenFileManager.deleteFileName()
            }
            MapType.SCHEME_MAP.value -> {
                binding.mapView.setTileSource(
                    MapTools.getGoogleMapTile(
                        requireContext(), binding.mapView, Pair("Google maps", "m")
                    )
                )
                osmdroidViewModel.saveCurrentMapState(mapType)
                offlineOpenFileManager.deleteFileName()
            }
            MapType.GOOGLE_HYB.value -> {
                binding.mapView.setTileSource(
                    MapTools.getGoogleMapTile(
                        requireContext(), binding.mapView, Pair("Google hybrid", "y")
                    )
                )
                osmdroidViewModel.saveCurrentMapState(mapType)
                offlineOpenFileManager.deleteFileName()
            }
            MapType.GOOGLE_SAT.value -> {
                binding.mapView.setTileSource(
                    MapTools.getGoogleMapTile(
                        requireContext(), binding.mapView, Pair("Google satellite", "s")
                    )
                )
                osmdroidViewModel.saveCurrentMapState(mapType)
                offlineOpenFileManager.deleteFileName()
            }
            MapType.NOKIA_SAT.value -> {
                binding.mapView.setTileSource(
                    MapTools.getHereMapTile(requireContext(), binding.mapView)
                )
            }
            MapType.OFFLINE.value -> {
                val offlineMapFileName = offlineOpenFileManager.getFileName()
                if (offlineMapFileName != null) openFile(
                    offlineMapFileName, binding.mapView, requireContext()
                )
                else offlineMode(binding.mapView, requireContext())
                osmdroidViewModel.saveCurrentMapState(mapType)
            }
            else -> {
                binding.mapView.setTileSource(
                    MapTools.getOSMMapTile(
                        requireContext(), binding.mapView
                    )
                )
                osmdroidViewModel.saveCurrentMapState(mapType)
                offlineOpenFileManager.deleteFileName()
                
            }
        }
    }
    
    private fun setupPolylines() {
        val zeroPoint = GeoPoint(0.0, 0.0)
        val polylinePointsForToFrontSight = listOf(droneMarker.position, frontSightMarker.position)
        val polylinePointsForCenter = listOf(droneMarker.position, zeroPoint)
        setPolyline(polylineToFrontSight, polylinePointsForToFrontSight, Color.GREEN)
        setPolyline(polylineToCenter, polylinePointsForCenter)
    }
    
    private fun setPolyline(polyline: Polyline, points: List<GeoPoint>, color: Int = Color.BLUE) {
        polyline.setPoints(points)
        polyline.color = color
        polyline.width = 4.0f
        binding.mapView.overlays.add(polyline)
        polylineToFrontSight.isVisible = true
    }
    
    private fun updatePolyline(polyline: Polyline, points: List<GeoPoint>) {
        polyline.setPoints(points)
        binding.mapView.invalidate()
    }
    
    override fun initTechnic() {
        osmdroidViewModel.getTechnics()
        osmdroidViewModel.technicListLiveData.observe(viewLifecycleOwner) {
            it?.let { list ->
                list.forEach { technic ->
                    val mark = Marker(binding.mapView)
                    drawMarker(mark, technic)
                    
                    listOfTechnic.add(mark)
                    technic.division?.let {
                        addClickListenerToMark(mark, technic)
                    }
                }
            }
            osmdroidViewModel.technicListLiveData.removeObservers(viewLifecycleOwner)
        }
    }
    
    override fun spawnTechnic(type: TechnicTypes, coords: Coordinates?, division: String) {
        if (coords != null) for (technic in listOfTechnic) {
            technic as Marker
            if (technic.position.latitude == coords.x && technic.position.longitude == coords.y) return
        }
        
        val cameraPosition = binding.mapView.mapCenter
        osmdroidViewModel.technicListLiveData.removeObservers(viewLifecycleOwner)
        if (!osmdroidViewModel.technicListLiveData.hasObservers()) osmdroidViewModel.getTechnics()
        var count = 0
        osmdroidViewModel.technicListLiveData.observe(this) { technicList ->
            osmdroidViewModel.technicListLiveData.removeObservers(this)
            count = technicList.size + 1
            val mark: Marker = if (coords != null) setMark(coords.x, coords.y, type)
            else setMark(cameraPosition.latitude, cameraPosition.longitude, type)

            val elevation = getElevation(
                roundToFourDecimalPlaces(mark.position.altitude),
                roundToFourDecimalPlaces(mark.position.longitude)
            ).toDouble()

            val technic = Technic(
                id = count, technicTypes = type, coordinates = Coordinates(
                    x = mark.position.latitude, y = mark.position.longitude, h = elevation
                ), division = division
            )

            addClickListenerToMark(mark, technic)
            osmdroidViewModel.saveTechnic(technic)
        }
    }
    
    private fun addClickListenerToMark(mark: Marker, technic: Technic) {
        mark.setOnMarkerClickListener { marker, mapView ->
            val targetFragment = TargetFragment(
                technic = technic,
                object : TargetFragmentCallback {
                    override fun onBroadcastButtonClick(destinationId: String, technic: Technic) {
                        sendMessage(destinationId, technic)
                    }

                    override fun deleteTechnic() {
                        binding.mapView.overlays.remove(mark)
                        listOfTechnic.remove(mark)
                        osmdroidViewModel.deleteTechnic(technic)
                        binding.mapView.invalidate()
                    }
                }
            )
            targetFragment.show(parentFragmentManager, "targFragment")
            true
        }
    }

    override fun findGeoPoint() {
        val findGeoPointFragment = FindGeoPointFragment(object : FindGeoPointCallback {
            override fun findGeoPoint(geoPoint: GeoPoint) {
                focusCamera(geoPoint)
            }
        })
        findGeoPointFragment.show(parentFragmentManager, "findGeoPointFragment")
    }

    override fun showAllTargets() { // TODO:
    }

    private fun setMark(
        latitude: Double, longitude: Double, type: TechnicTypes
    ): Marker {
        var mark = Marker(binding.mapView)
        mark = drawMarker(
            mark, Technic(
                coordinates = Coordinates(x = latitude, y = longitude), technicTypes = type
            )
        )
        listOfTechnic.add(mark)
        return mark
    }

    private fun drawMarker(mark: Marker? = null, technic: Technic): Marker {
        var marker = mark
        if (marker == null) marker = Marker(binding.mapView)
        marker.position = GeoPoint(technic.coordinates.x, technic.coordinates.y)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        binding.mapView.overlays.add(marker)
        marker.icon = getDrawable(requireContext(), ImageTypes.imageMap[technic.technicTypes]!!)
        binding.mapView.invalidate()

        return marker
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

        getData.setDroneData(entities[0], Math.toDegrees(correctionAngRad))

        val frontSightGeoPoint = GeoPoint(getData.target.lat, getData.target.lon)
        frontSightMarker.position = frontSightGeoPoint
        updatePolyline(
            polylineToFrontSight, listOf(droneMarker.position, frontSightMarker.position)
        )

        if (entities[0].calc_target == 4) {
            pointCalibration.rememberPoint(
                GeoPoint(entities[0].lat, entities[0].lon),
                entities[0].asim
            )
            Toast.makeText(requireContext(), "?????????? ????????????!", Toast.LENGTH_SHORT).show()
            binding.intersectionButton.visibility = View.VISIBLE
        }
        var marker: Marker
        if (entities[0].calc_target == 2) {
            marker = drawMarker(
                mark = null, technic = Technic(
                    coordinates = Coordinates(
                        frontSightGeoPoint.latitude,
                        frontSightGeoPoint.longitude
                    ),
                    technicTypes = TechnicTypes.AIM
                )
            )
            listOfTechnic.add(marker)
        }
        if (entities[0].calc_target == 3) {
            marker = drawMarker(
                mark = null, technic = Technic(
                    coordinates = Coordinates(
                        frontSightGeoPoint.latitude,
                        frontSightGeoPoint.longitude
                    ),
                    technicTypes = TechnicTypes.DISRUPTION
                )
            )
            listOfTechnic.add(marker)
        }

        showGeoInformation(binding, cameraTarget, droneMarker.position)

        if (entities[0].calc_target == 1) {
            val divisionHandler = requireActivity() as DivisionHandler
            if (divisionHandler.checkDivision(requireContext())) {
                val division = divisionHandler.getDivision(requireContext())
                osmdroidViewModel.getTargetCoordinates(entities)
                osmdroidViewModel.targetLiveData.observe(this) { findTarget ->
                    spawnTechnic(
                        TechnicTypes.ANOTHER,
                        Coordinates(x = findTarget.lat, y = findTarget.lon),
                        division!!
                    )
                }
            }
        }
    }

    private fun findCorrectionAngle() {
        val polylineToCenterAzimuth =
            MapTools.angleBetween(
                correctionPolyline.actualPoints[0],
                correctionPolyline.actualPoints[1]
            )
        val polylineToFrontSightAzimuth = MapTools.angleBetween(
            polylineToFrontSight.actualPoints[0], polylineToFrontSight.actualPoints[1]
        )
        val correctionAngDeg = polylineToCenterAzimuth - polylineToFrontSightAzimuth
        correctionAngRad = Math.toRadians(correctionAngDeg)
        var angle = round(correctionAngDeg * 100) / 100.0
        if (angle > 180)
            angle -= 360
        if (angle < -180)
            angle += 360
        Toast.makeText(requireContext(), "???????? ??????????????????: " + angle, Toast.LENGTH_SHORT).show()
    }

    private fun focusCamera(point: GeoPoint) {
        binding.mapView.controller.animateTo(point)
        binding.mapView.controller.zoomIn()
    }

    override fun showLocationDialog() {
        showLocationDialog(requireContext(), object : LocationDialogCallback {
            override fun focusCamera() {
                focusCamera(frontSightMarker.position)
            }

            override fun findMyLocation() {
                if (checkGPS(requireActivity())) locationOverlay?.let {
                    if (it.myLocation != null) focusCamera(it.myLocation)
                }
            }
        })
    }

    private fun initMyLocation() {
        val provider = GpsMyLocationProvider(requireContext())
        provider.addLocationSource(LocationManager.GPS_PROVIDER)
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
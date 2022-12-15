package com.example.dronevision.presentation.ui.yandex_map

import android.bluetooth.BluetoothManager
import android.content.Context.BLUETOOTH_SERVICE
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.AbonentDialogFragment
import com.example.dronevision.App
import com.example.dronevision.R
import com.example.dronevision.databinding.FragmentYandexMapBinding
import com.example.dronevision.domain.model.Coordinates
import com.example.dronevision.domain.model.TechnicTypes
import com.example.dronevision.presentation.model.BluetoothListItem
import com.example.dronevision.presentation.model.Message
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.ImageTypes
import com.example.dronevision.presentation.ui.bluetooth.BluetoothCallback
import com.example.dronevision.presentation.ui.bluetooth.BluetoothConnection
import com.example.dronevision.presentation.ui.bluetooth.BluetoothReceiver
import com.example.dronevision.presentation.ui.bluetooth.SelectBluetoothFragment
import com.example.dronevision.utils.SpawnTechnic
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.RotationType
import com.yandex.runtime.image.ImageProvider
import javax.inject.Inject

class YandexMapFragment : Fragment(), BluetoothReceiver.MessageListener {
    
    private lateinit var binding: FragmentYandexMapBinding
    private lateinit var marker: PlacemarkMapObject
    
    lateinit var bluetoothConnection: BluetoothConnection
    private var dialog: SelectBluetoothFragment? = null
    
    private lateinit var viewModel: YandexMapViewModel
    
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
        
        setupBluetooth()
        initMarker()
        setupOptionsMenu()
        initTechnic()
        
        SpawnTechnic.spawnTechnicLiveData.observe(viewLifecycleOwner) {
            spawnTechnic(it.imageRes, it.type)
        }
        
        return binding.root
    }
    
    private fun initTechnic() {
        viewModel.getTechnics()
        viewModel.technicListLiveData.observe(viewLifecycleOwner) {
            it?.let { list ->
                list.forEach { technic ->
                    val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
                    mapObjCollection.addPlacemark(
                        Point(technic.coords.x, technic.coords.y),
                        ImageProvider.fromResource(
                            requireContext(),
                            ImageTypes.imageMap[technic.type]!!
                        )
                    )
                }
            }
        }
    }
    
    private fun spawnTechnic(@DrawableRes imageRes: Int, type: TechnicTypes) {
        val cameraPositionTarget = binding.mapView.map.cameraPosition.target
        val mapObjCollection = binding.mapView.map.mapObjects.addCollection()
        val mark = mapObjCollection.addPlacemark(
            Point(cameraPositionTarget.latitude, cameraPositionTarget.longitude),
            ImageProvider.fromResource(requireContext(), imageRes)
        )
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
    
    private fun addMarker(
        latitude: Double,
        longitude: Double,
        asim: Float,
    ): PlacemarkMapObject {
        marker.direction = asim
        marker.geometry = Point(latitude, longitude)
        marker.isVisible = true
        marker.addTapListener { mapObject, point ->
            return@addTapListener true
        }
        // markerTapListener?.let { marker.addTapListener(it) }
        return marker
    }
    
    private fun showLocationFromDrone(message: String) {
        val array = message.split(",")
        val lat = array[0].toDouble()
        val lon = array[1].toDouble()
        val alt = array[2].toDouble()
        val asim = array[3].toFloat()
        
        addMarker(lat, lon, asim)
    }
    
    private fun setupBluetooth() {
        val bluetoothManager = activity?.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothConnection = BluetoothConnection(
            bluetoothAdapter,
            context = requireContext(), listener = this
        )
        
        dialog = SelectBluetoothFragment(bluetoothAdapter, object : BluetoothCallback {
            override fun onClick(item: BluetoothListItem) {
                item.let {
                    bluetoothConnection.connect(it.mac)
                    // bluetoothConnection.sendMessage("Тест")
                }
                dialog?.dismiss()
            }
        })
    }
    
    override fun onReceive(message: Message) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message.message, Toast.LENGTH_LONG).show()
            if (!message.isSystem)
                showLocationFromDrone(message.message)
        }
    }
    
    private fun setupOptionsMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)
            }
            
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.bluetooth -> {
                        dialog?.show(parentFragmentManager, "ActionBottomDialog")
                        true
                    }
                    R.id.actionGeoLocation -> {
                        showLocationDialog()
                        Toast.makeText(requireContext(), "fragment", Toast.LENGTH_LONG).show()
                        true
                    }
                    R.id.abonentAddItem -> {
                        val abonentDialogFragment = AbonentDialogFragment()
                        abonentDialogFragment.show(parentFragmentManager, "myDialog")
                        true
                    }
                    R.id.removeAll -> {
                        binding.mapView.map.mapObjects.clear()
                        viewModel.deleteAll()
                        initMarker()
                        true
                    }
                    else -> false
                }
            }
        }, requireActivity(), Lifecycle.State.RESUMED)
    }
    
    private fun initMarker() {
        marker = binding.mapView.map.mapObjects.addPlacemark(
            Point(0.0, 0.0),
            ImageProvider.fromResource(requireContext(), R.drawable.gps_tacker2)
        )
        marker.setIconStyle(IconStyle().setRotationType(RotationType.ROTATE))
        marker.isVisible = false
    }
    
    private fun showLocationDialog() {
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
                Point(marker.geometry.latitude, marker.geometry.longitude),
                12.0f,
                0.0f,
                0.0f
            ),
            Animation(Animation.Type.SMOOTH, 1.0f), null
        )
    }
}

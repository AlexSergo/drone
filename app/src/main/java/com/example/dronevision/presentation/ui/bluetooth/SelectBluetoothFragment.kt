package com.example.dronevision.presentation.ui.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dronevision.databinding.FragmentSelectBluetoothBinding
import com.example.dronevision.presentation.model.bluetooth.BluetoothListItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SelectBluetoothFragment(
    private var bluetoothAdapter: BluetoothAdapter?,
    private val bluetoothCallback: BluetoothCallback
) :
    BottomSheetDialogFragment() {
    
    private lateinit var binding: FragmentSelectBluetoothBinding
    private lateinit var recyclerViewAdapter: BluetoothRecyclerViewAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectBluetoothBinding.inflate(layoutInflater)
        binding.recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)
        recyclerViewAdapter = BluetoothRecyclerViewAdapter(bluetoothCallback)
        binding.recyclerView.adapter = recyclerViewAdapter
        getPairedDevices()
        return binding.root
    }

    private fun getPairedDevices(){
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val tempList = ArrayList<BluetoothListItem>()
        pairedDevices?.forEach {
            tempList.add(BluetoothListItem(name = it.name, mac = it.address))
        }
        recyclerViewAdapter.setData(tempList)
    }

    companion object{
        const val DEVICE_KEY = "device_key"
    }
}
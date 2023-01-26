package com.example.dronevision.presentation.ui.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dronevision.databinding.FragmentSelectBluetoothBinding
import com.example.dronevision.presentation.model.bluetooth.BluetoothListItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SelectBluetoothFragment(
    private var bluetoothAdapter: BluetoothAdapter?,
    private val bluetoothCallback: BluetoothCallback
) : BottomSheetDialogFragment() {
    
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

    @SuppressLint("MissingPermission")
    private fun getPairedDevices(){
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val tempList = ArrayList<BluetoothListItem>()
        pairedDevices?.forEach {
            tempList.add(BluetoothListItem(name = it.name, mac = it.address))
        }
        recyclerViewAdapter.setData(tempList)
    }
}
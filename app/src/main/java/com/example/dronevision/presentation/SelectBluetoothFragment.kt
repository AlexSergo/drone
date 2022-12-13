package com.example.dronevision.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemServiceName
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dronevision.databinding.FragmentSelectBluetoothBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SelectBluetoothFragment(private var bluetoothAdapter: BluetoothAdapter?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSelectBluetoothBinding
    private lateinit var recyclerViewAdapter: BluetoothRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectBluetoothBinding.inflate(layoutInflater)
        binding.recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)
        recyclerViewAdapter = BluetoothRecyclerViewAdapter()
        binding.recyclerView.adapter = recyclerViewAdapter
        getPairedDevices()
        return binding.root
    }
}
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dronevision.databinding.FragmentSelectBluetoothBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SelectBluetoothFragment(private var bluetoothAdapter: BluetoothAdapter?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSelectBluetoothBinding
    private lateinit var recyclerViewAdapter: BluetoothRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSelectBluetoothBinding.inflate(layoutInflater)
        recyclerViewAdapter = BluetoothRecyclerViewAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = recyclerViewAdapter
        getPairedDevices()
    }

    private fun getPairedDevices(){
        val pairedDevices: Set<BluetoothDevice>?
            pairedDevices = bluetoothAdapter?.bondedDevices
            val tempList = ArrayList<BluetoothListItem>()
            pairedDevices?.forEach {
                Log.d("MyLog", "Name ${it.name}")
                tempList.add(BluetoothListItem(name = it.name, mac = it.address))
        }
            recyclerViewAdapter.submitList(tempList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectBluetoothBinding.inflate(layoutInflater)

        return binding.root
    }
}
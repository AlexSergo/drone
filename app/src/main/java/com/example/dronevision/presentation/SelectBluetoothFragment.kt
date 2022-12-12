package com.example.dronevision.presentation

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import com.example.dronevision.databinding.FragmentSelectBluetoothBinding


class SelectBluetoothFragment : Fragment() {

    private lateinit var binding: FragmentSelectBluetoothBinding
    private var bluetoothAdapter: BluetoothAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        }

    private fun init() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    private fun getPairedDevices(){
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectBluetoothBinding.inflate(layoutInflater)

        return binding.root
    }
}
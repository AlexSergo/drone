package com.example.dronevision

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dronevision.databinding.FragmentControlBluetoothBinding
import java.util.*


class ControlBluetoothFragment : Fragment() {

    private lateinit var binding: FragmentControlBluetoothBinding

    companion object{
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var bluetoothSocket: BluetoothSocket? = null
        lateinit var progress: ProgressDialog
        lateinit var bluetoothAdapter: BluetoothAdapter
        var isConnected = false
        lateinit var address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentControlBluetoothBinding.inflate(layoutInflater)
        return binding.root
    }
}
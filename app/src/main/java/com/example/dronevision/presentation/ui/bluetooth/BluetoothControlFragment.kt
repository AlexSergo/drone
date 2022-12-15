package com.example.dronevision.presentation.ui.bluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dronevision.databinding.FragmentBluetoothControlBinding


class BluetoothControlFragment : Fragment() {
    
    private lateinit var binding: FragmentBluetoothControlBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBluetoothControlBinding.inflate(inflater, container, false)
        return binding.root
    }
    
}
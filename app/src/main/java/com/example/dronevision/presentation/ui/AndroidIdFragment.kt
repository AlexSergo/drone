package com.example.dronevision.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.dronevision.databinding.FragmentAndroidIdBinding
import com.example.dronevision.utils.Device

class AndroidIdFragment : DialogFragment() {

    private lateinit var binding: FragmentAndroidIdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAndroidIdBinding.inflate(layoutInflater)
        binding.androidId.text = Device.id
        return binding.root
    }

}
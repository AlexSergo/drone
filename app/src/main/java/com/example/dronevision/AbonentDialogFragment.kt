package com.example.dronevision

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.dronevision.databinding.FragmentAbonentDialogBinding


class AbonentDialogFragment : DialogFragment() {

    lateinit var binding: FragmentAbonentDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAbonentDialogBinding.inflate(layoutInflater)
        return binding.root
    }
}
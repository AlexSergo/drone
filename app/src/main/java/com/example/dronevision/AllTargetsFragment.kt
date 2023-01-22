package com.example.dronevision

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dronevision.databinding.FragmentAllTargetsBinding

class AllTargetsFragment : Fragment() {
    
    private lateinit var binding: FragmentAllTargetsBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllTargetsBinding.inflate(inflater, container, false)
        return binding.root
    }
}
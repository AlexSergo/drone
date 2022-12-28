package com.example.dronevision.presentation.ui.targ

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.dronevision.databinding.FragmentTargBinding
import com.example.dronevision.presentation.model.Technic

class TargFragment(private val technic: Technic, private val targetFragmentCallback: TargetFragmentCallback) : DialogFragment() {
    
    private lateinit var binding: FragmentTargBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTargBinding.inflate(inflater, container, false)
        binding.latitudeValue.text = technic.coords.x.toString()
        binding.longitudeValue.text = technic.coords.y.toString()
        binding.findBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Кнопки пока не работают", Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
        }
        binding.broadcastBtn.setOnClickListener {
            targetFragmentCallback.onBroadcastButtonClick(technic)
            dialog?.dismiss()
        }
        binding.deleteBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Кнопки пока не работают", Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
        }
        return binding.root
    }

    interface TargetFragmentCallback{
        fun onBroadcastButtonClick(technic: Technic)
    }
}
package com.example.dronevision.presentation.ui.find_location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.dronevision.databinding.FragmentFindGeoPointBinding
import org.osmdroid.util.GeoPoint

class FindGeoPointFragment(private val callback: FindGeoPointCallback) : DialogFragment() {
    
    private lateinit var binding: FragmentFindGeoPointBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindGeoPointBinding.inflate(inflater, container, false)
        
        binding.findBtn.setOnClickListener {
            try {
                val latitude = binding.latitudeValue.text.toString().toDouble()
                val longitude = binding.longitudeValue.text.toString().toDouble()
                val geoPoint = GeoPoint(latitude, longitude)
                callback.findGeoPoint(geoPoint)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Недопустимые значения", Toast.LENGTH_LONG).show()
            }
            dialog?.dismiss()
        }
        return binding.root
    }
}
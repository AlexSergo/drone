package com.example.dronevision.presentation.ui.find_location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import com.example.dronevision.databinding.FragmentFindGeoPointBinding
import com.example.dronevision.utils.Constants
import com.example.dronevision.utils.NGeoCalc
import org.osmdroid.util.GeoPoint
import kotlin.math.roundToInt

class FindGeoPointFragment(private val callback: FindGeoPointCallback) : DialogFragment() {
    
    private var bMinSave = 0
    private var bSave = 0.0
    private var bSecSave = 0
    private var lMinSave = 0
    private var lSave = 0.0
    private var lSecSave = 0
    private var xSave = 0
    private var ySave = 0
    private var searchType = 0
    private lateinit var binding: FragmentFindGeoPointBinding
    private val typeStringList = arrayOf(
        "Широта/долгота (градусы)",
        "Широта/долгота (град/мин)",
        "Широта/долгота (град/мин/сек)",
        "Прямоугольные (метры)"
    )
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFindGeoPointBinding.inflate(inflater, container, false)
        
        dialog?.window?.setSoftInputMode(2)
        setSearchTypeView()
        
        binding.searchTypeButton.setOnClickListener {
            val searchTypeBuilder = AlertDialog.Builder(requireContext())
            searchTypeBuilder.setTitle("Поиск на карте" as CharSequence)
            searchTypeBuilder.setItems(typeStringList) { _, which ->
                searchType = which
                setSearchTypeView()
            }.show()
        }
        
        binding.mapSearchButton.setOnClickListener {
            val geoPoint: GeoPoint?
            val bx: Double =
                string2Double(binding.bxEditText.text.toString())
            val ly: Double =
                string2Double(binding.lyEditText.text.toString())
            if (searchType == 0) {
                bSave = bx
                lSave = ly
                geoPoint = GeoPoint(bx, ly)
            } else if (searchType == 1) {
                val bMinutes2: Int = getNumericEditValue(binding.bMinutesEditText)
                val lMinutes: Int = getNumericEditValue(binding.lMinutesEditText)
                bSave = bx
                lSave = ly
                bMinSave = bMinutes2
                bSecSave = 0
                geoPoint = GeoPoint(
                    dmm2ddd(bx.roundToInt(), bMinutes2.toDouble()),
                    dmm2ddd(ly.roundToInt(), lMinutes.toDouble())
                )
            } else if (searchType == 2) {
                val bMinutes3: Int = getNumericEditValue(binding.bMinutesEditText)
                val bSeconds: Int = getNumericEditValue(binding.bSecEditText)
                val lMinutes2: Int = getNumericEditValue(binding.lMinutesEditText)
                val lSeconds: Int = getNumericEditValue(binding.lSecEditText)
                bSave = bx
                lSave = ly
                bMinSave = bMinutes3
                bSecSave = bSeconds
                lMinSave = lMinutes2
                lSecSave = lSeconds
                geoPoint = GeoPoint(
                    dms2ddd(bx.roundToInt(), bMinutes3.toDouble(), bSeconds.toDouble()),
                    dms2ddd(ly.roundToInt(), lMinutes2.toDouble(), lSeconds.toDouble())
                )
            } else if (searchType != 3) {
                geoPoint = null
            } else {
                xSave = bx.toInt()
                ySave = ly.toInt()
                val b = doubleArrayOf(0.0)
                val l = doubleArrayOf(0.0)
                NGeoCalc().planeToWgs84(b, l, doubleArrayOf(0.0), bx, ly, 0.0)
                geoPoint =
                    GeoPoint(NGeoCalc.radiansToDegrees(b[0]), NGeoCalc.radiansToDegrees(l[0]))
            }
            if (geoPoint == null || geoPoint.latitude == 0.0 || geoPoint.longitude == 0.0) {
                Toast.makeText(requireContext(), "Задайте координаты для поиска", Toast.LENGTH_LONG).show()
            } else {
                callback.findGeoPoint(geoPoint)
                dialog?.dismiss()
            }
        }
        
        return binding.root
    }
    
    private fun setSearchTypeView() {
        binding.searchTypeButton.text = typeStringList[searchType]
        when (searchType) {
            0 -> {
                binding.bMinutesEditText.isGone = true
                binding.bSecEditText.isGone = true
                binding.lMinutesEditText.isGone = true
                binding.lSecEditText.isGone = true
                setHintSk42()
            }
            1 -> {
                binding.bMinutesEditText.isGone = true
                binding.bSecEditText.isGone = false
                binding.lMinutesEditText.isGone = true
                binding.lSecEditText.isGone = false
                setHintSk42()
            }
            2 -> {
                binding.bMinutesEditText.isGone = false
                binding.bSecEditText.isGone = false
                binding.lMinutesEditText.isGone = false
                binding.lSecEditText.isGone = false
                setHintSk42()
            }
            3 -> {
                binding.bMinutesEditText.isGone = true
                binding.bSecEditText.isGone = true
                binding.lMinutesEditText.isGone = true
                binding.lSecEditText.isGone = true
                binding.bxEditText.hint = "X, метры"
                binding.lyEditText.hint = "Y, метры"
            }
        }
    }
    
    private fun setHintSk42() {
        binding.bxEditText.hint = "Широта, градусы"
        binding.lyEditText.hint = "Долгота, градусы"
    }
    
    private fun string2Double(_s: String): Double {
        var s = _s
        var negativeFlag = false
        if (s.startsWith("-")) {
            s = s.replace("-", "")
            negativeFlag = true
        }
        return try {
            val d = s.replace(",", Constants.PROPERTY_DIVIDER).toDouble()
            if (negativeFlag) {
                d * -1.0
            } else d
        } catch (e: NumberFormatException) {
            0.0
        }
    }
    
    private fun getNumericEditValue(edit: EditText): Int {
        var negative = false
        var numString = edit.text.toString()
        if (numString.startsWith("-")) {
            numString = numString.replace("-", "")
            negative = true
        }
        return try {
            val value = numString.toInt()
            if (negative) {
                value * -1
            } else value
        } catch (e: java.lang.NumberFormatException) {
            0
        }
    }
    
    private fun dms2ddd(degrees: Int, minutes: Double, seconds: Double): Double {
        if (minutes >= 60.0 || seconds >= 60.0) {
            return 0.0
        }
        val d = degrees.toDouble()
        java.lang.Double.isNaN(d)
        return d + minutes / 60.0 + seconds / 3600.0
    }
    
    private fun dmm2ddd(degrees: Int, minutes: Double): Double {
        if (minutes >= 60.0) {
            return 0.0
        }
        val d = degrees.toDouble()
        java.lang.Double.isNaN(d)
        return d + minutes / 60.0
    }
}
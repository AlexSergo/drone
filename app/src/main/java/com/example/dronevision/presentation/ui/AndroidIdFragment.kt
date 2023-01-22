package com.example.dronevision.presentation.ui

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.ClipboardManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.dronevision.databinding.FragmentAndroidIdBinding
import com.example.dronevision.presentation.delegates.BluetoothHandler
import com.example.dronevision.presentation.ui.bluetooth.BluetoothConnection
import com.example.dronevision.presentation.ui.bluetooth.BluetoothReceiver
import com.example.dronevision.presentation.ui.bluetooth.ConnectThread
import com.example.dronevision.presentation.ui.bluetooth.SelectBluetoothFragment
import com.example.dronevision.utils.Device

class AndroidIdFragment : DialogFragment() {

    private lateinit var binding: FragmentAndroidIdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAndroidIdBinding.inflate(layoutInflater)
        binding.androidId.text = Device.id
        binding.copyButton.setOnClickListener {
           Device.setClipboard(requireContext(), Device.id)
            Toast.makeText(requireContext(), "Скопировано в буфер обмена!", Toast.LENGTH_SHORT)
                .show()
        }
        val bluetoothHandler = requireActivity() as BluetoothHandler
        bluetoothHandler.acceptBluetoothConnection()
        binding.sendButton.setOnClickListener {
            bluetoothHandler.sendMessage("[ID]" + Device.id)
        }
        return binding.root
    }
}
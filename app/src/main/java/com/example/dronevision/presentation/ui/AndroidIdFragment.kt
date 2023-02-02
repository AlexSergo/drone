package com.example.dronevision.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.example.dronevision.PasswordChangeDialogFragment
import com.example.dronevision.data.source.local.prefs.KeyManager
import com.example.dronevision.databinding.FragmentAndroidIdBinding
import com.example.dronevision.presentation.delegates.BluetoothHandler
import com.example.dronevision.presentation.delegates.DivisionHandler
import com.example.dronevision.utils.Constants
import com.example.dronevision.utils.Device
import com.example.dronevision.data.source.local.prefs.SharedPreferences
import com.example.dronevision.utils.AESEncyption

class AndroidIdFragment : DialogFragment() {

    private lateinit var binding: FragmentAndroidIdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val keyPrefs = KeyManager(requireContext())
        binding = FragmentAndroidIdBinding.inflate(layoutInflater)
        binding.androidId.text = Device.id
        binding.androidId.setOnClickListener {
           Device.setClipboard(requireContext(), Device.id)
            Toast.makeText(requireContext(), "Скопировано в буфер обмена!", Toast.LENGTH_SHORT)
                .show()
        }
        binding.passwordChangeButton.setOnClickListener {
            val changePasswordDialog = PasswordChangeDialogFragment()
            val opener = requireActivity() as OpenDialogCallback
            opener.openDialog(changePasswordDialog)
        }

        val bluetoothHandler = requireActivity() as BluetoothHandler
        bluetoothHandler.acceptBluetoothConnection()
        binding.sendButton.setOnClickListener {
            bluetoothHandler.sendMessage("[ID]" + Device.id)
        }
        binding.saveButton.setOnClickListener {
            val sharedPreferences = SharedPreferences(requireContext())
            sharedPreferences.save(Constants.DIVISION_KEY, binding.divisionEditText.text.toString())
            val key = binding.secretKeyEditText.text.toString()
            if (key != "") {
                keyPrefs.saveKey(key)
                AESEncyption.secretKey = key
            }
            dialog?.dismiss()
        }
        val division = (requireActivity() as DivisionHandler).getDivision(requireContext())
        binding.divisionEditText.setText(division)
        binding.secretKeyEditText.setText(keyPrefs.getKey())
        return binding.root
    }
}
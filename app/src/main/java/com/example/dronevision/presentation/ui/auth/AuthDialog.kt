package com.example.dronevision.presentation.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.dronevision.data.source.local.prefs.PasswordManager
import com.example.dronevision.data.source.local.prefs.SharedPreferences
import com.example.dronevision.databinding.AuthDialogBinding
import com.example.dronevision.utils.Device
import com.example.dronevision.utils.Hash

class AuthDialog(private val callback: AuthDialogCallback) : DialogFragment() {
    
    private lateinit var binding: AuthDialogBinding
    
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AuthDialogBinding.inflate(inflater, container, false)
        val displayMetrics = resources.displayMetrics
        binding.authDialog.minHeight =
            displayMetrics.heightPixels - displayMetrics.heightPixels / 10
        binding.authDialog.minWidth = displayMetrics.widthPixels - displayMetrics.widthPixels / 10
        dialog?.window?.setDimAmount(1f)
        binding.deviceId.text = Device.getDeviceId(requireContext())
        binding.deviceId.setOnClickListener {
            Device.setClipboard(requireContext(), binding.deviceId.text.toString())
        }
        
        val passwordManager = PasswordManager(requireContext())
        val savedPassword = passwordManager.getPassword()
    
        val sharedPreferences = SharedPreferences(requireContext())
        val deviceId = Device.getDeviceId(requireContext())
        val savedDeviceId = sharedPreferences.getValue("AUTH_TOKEN")
        
        if (savedDeviceId == Hash.md5(deviceId + "крокодил") && savedPassword != null) {
            binding.editPasswordConfirm.isVisible = false
            binding.enterBtn.setOnClickListener {
                val password = binding.editPassword.text.toString()
                if (Hash.md5(password) == savedPassword) dialog?.dismiss()
                else makeToastWrongPassword()
            }
        } else {
            binding.enterBtn.setOnClickListener {
                val password = binding.editPassword.text.toString()
                val passwordConfirm = binding.editPasswordConfirm.text.toString()
        
                if (password.isEmpty()) makeToastEmptyPassword()
                else if (password.length < 4) makeToastSmallPassword()
                else if (password.length > 10) makeToastLongPassword()
                else if (password != passwordConfirm) makeToastPasswordsNotMatch()
                else callback.checkRegistration(deviceId, Hash.md5(password))
            }
        }
        return binding.root
    }
    
    private fun makeToastWrongPassword() {
        Toast.makeText(
            requireContext(),
            "Неверный пароль",
            Toast.LENGTH_LONG
        ).show()
    }
    
    private fun makeToastEmptyPassword() {
        Toast.makeText(requireContext(), "Пароль не может быть пустым", Toast.LENGTH_LONG).show()
    }
    
    private fun makeToastSmallPassword() {
        Toast.makeText(
            requireContext(),
            "Пароль не может быть короче 4 символов",
            Toast.LENGTH_LONG
        ).show()
    }
    
    private fun makeToastLongPassword() {
        Toast.makeText(
            requireContext(),
            "Пароль не может быть длиннее 10 символов",
            Toast.LENGTH_LONG
        ).show()
    }
    
    private fun makeToastPasswordsNotMatch() {
        Toast.makeText(
            requireContext(),
            "Пароль не совпадают",
            Toast.LENGTH_LONG
        ).show()
    }
}
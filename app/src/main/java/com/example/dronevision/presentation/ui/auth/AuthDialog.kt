package com.example.dronevision.presentation.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import com.example.dronevision.presentation.delegates.PasswordValidator
import com.example.dronevision.presentation.delegates.PasswordValidatorImpl
import com.example.dronevision.utils.Device
import com.example.dronevision.utils.Hash


class AuthDialog(private val callback: AuthDialogCallback) : DialogFragment(), PasswordValidator by PasswordValidatorImpl() {
    
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

        binding.supportButton.setOnClickListener {
            goToSupport()
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
                else makeToastWrongPassword(requireContext())
            }
        } else {
            binding.enterBtn.setOnClickListener {
                val password = binding.editPassword.text.toString()
                val passwordConfirm = binding.editPasswordConfirm.text.toString()
        
                if (password.isEmpty()) makeToastEmptyPassword(requireContext())
                else if (password.length < 4) makeToastSmallPassword(requireContext())
                else if (password.length > 10) makeToastLongPassword(requireContext())
                else if (password != passwordConfirm) makeToastPasswordsNotMatch(requireContext())
                else callback.checkRegistration(deviceId, Hash.md5(password))
            }
        }
        return binding.root
    }

    private fun goToSupport() {
        val uriUrl: Uri = Uri.parse("https://t.me/svohelp2023")
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
    }
}
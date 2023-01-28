package com.example.dronevision.presentation.ui.auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.dronevision.data.source.local.prefs.FirstRunAppManager
import com.example.dronevision.data.source.local.prefs.LoginManager
import com.example.dronevision.data.source.local.prefs.PasswordManager
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
    
        binding.editPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        binding.editPasswordConfirm.transformationMethod = HideReturnsTransformationMethod.getInstance()
        
        val loginManager = LoginManager(requireContext())
        val passwordManager = PasswordManager(requireContext())
        val firstRunAppManager = FirstRunAppManager(requireContext())
        
        val isFirstRunApp = firstRunAppManager.isFirstRunApp()
        val savedLogin = loginManager.getLogin()
        val savedPassword = passwordManager.getPassword()
        
        if (!isFirstRunApp || savedLogin == null || savedPassword == null) {
            val deviceId = Device.getDeviceId(requireContext())
            val futureLoginMd5 = Hash.md5(deviceId + "крокодил")
            val futureLogin = futureLoginMd5.substring(3, 9)
            binding.editLogin.setText("Логин: $futureLogin")
            binding.editLogin.isEnabled = false
            binding.message.text = "Придумайте пароль и запишите логин $futureLogin. " +
              "Эти данные будут требоваться при каждом входе в приложение"
            binding.enterBtn.setOnClickListener {
                val password = binding.editPassword.text.toString()
                val passwordConfirm = binding.editPasswordConfirm.text.toString()
                if (password.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Пароль не может быть пустым",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (password.length < 4) {
                    Toast.makeText(
                        requireContext(),
                        "Пароль не может быть короче 4 символов",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (password.length > 10) {
                    Toast.makeText(
                        requireContext(),
                        "Пароль не может быть длиннее 10 символов",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (password != passwordConfirm) {
                    Toast.makeText(
                        requireContext(),
                        "Пароль не совпадают",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    loginManager.addLogin(futureLogin)
                    passwordManager.addPassword(password)
                    dialog?.dismiss()
                }
            }
        } else {
            binding.editPasswordConfirm.isVisible = false
            binding.enterBtn.setOnClickListener {
                val login = binding.editLogin.text.toString()
                val password = binding.editPassword.toString()
                if (login == savedLogin || password == savedPassword) {
                    dialog?.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Неверный логин или пароль", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        return binding.root
    }
}
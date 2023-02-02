package com.example.dronevision

import android.content.Context
import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.dronevision.data.source.local.prefs.PasswordManager
import com.example.dronevision.databinding.FragmentPasswordChangeDialogBinding
import com.example.dronevision.presentation.delegates.PasswordValidator
import com.example.dronevision.presentation.delegates.PasswordValidatorImpl
import com.example.dronevision.utils.Hash


class PasswordChangeDialogFragment() : DialogFragment(), PasswordValidator by PasswordValidatorImpl() {

    private lateinit var binding: FragmentPasswordChangeDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordChangeDialogBinding.inflate(layoutInflater)
        binding.saveButton.setOnClickListener {
            val oldPassword = binding.editPassword.text.toString()
            val newPassword = binding.editPasswordNew.text.toString()
            val newPasswordConfirm = binding.editPasswordNewConfirm.text.toString()
            if (oldPassword == newPassword)
                Toast.makeText(requireContext(), "Пароль должен отличаться!", Toast.LENGTH_SHORT)
                    .show()
            if (newPassword.isEmpty()) makeToastEmptyPassword(requireContext())
            else if (newPassword.length < 4) makeToastSmallPassword(requireContext())
            else if (newPassword.length > 10) makeToastLongPassword(requireContext())
            else if (newPassword != newPasswordConfirm) makeToastPasswordsNotMatch(requireContext())
            else{
                val passwordManager = PasswordManager(requireContext())
                passwordManager.deletePassword()
                passwordManager.addPassword(Hash.md5(newPassword))
                dismiss()
            }
        }
        return binding.root
    }
}
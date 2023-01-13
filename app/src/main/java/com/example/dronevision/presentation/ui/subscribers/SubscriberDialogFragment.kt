package com.example.dronevision.presentation.ui.subscribers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.App
import com.example.dronevision.databinding.FragmentSubscriberDialogBinding
import com.example.dronevision.presentation.model.Subscriber
import javax.inject.Inject


class SubscriberDialogFragment(private val id: String? = null) : DialogFragment() {

    lateinit var binding: FragmentSubscriberDialogBinding
    private lateinit var viewModel: SubscriberViewModel
    @Inject
    lateinit var viewModelFactory: SubscriberViewModelFactory

    private fun inject() {
        (requireContext().applicationContext as App).appComponent.inject(this)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[SubscriberViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubscriberDialogBinding.inflate(layoutInflater)

        if (id != null)
            binding.editTextID.setText(id.substring(id.indexOf("]") + 1))

        binding.saveButton.setOnClickListener {
            if ( binding.editTextName.text.toString() != ""
                && binding.editTextID.text.toString() != "")
            viewModel.saveSubscriber(
                Subscriber(id = binding.editTextID.text.toString(),
                    name = binding.editTextName.text.toString()))
        }
        return binding.root
    }
}
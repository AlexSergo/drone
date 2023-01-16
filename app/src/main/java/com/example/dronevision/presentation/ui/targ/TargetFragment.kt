package com.example.dronevision.presentation.ui.targ

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.dronevision.databinding.FragmentTargBinding
import com.example.dronevision.presentation.delegates.BluetoothHandler
import com.example.dronevision.presentation.model.Subscriber
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.subscribers.SubscriberListCallback
import com.example.dronevision.presentation.ui.subscribers.SubscriberListDialog

class TargetFragment(private val technic: Technic,
                     private val targetFragmentCallback: TargetFragmentCallback) : DialogFragment() {
    
    private lateinit var binding: FragmentTargBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bluetoothHandler = requireActivity() as BluetoothHandler
        bluetoothHandler.acceptBluetoothConnection()
        binding = FragmentTargBinding.inflate(inflater, container, false)
        binding.header.text = technic.technicTypes.name
        binding.latitudeValue.text = technic.coordinates.x.toString()
        binding.longitudeValue.text = technic.coordinates.y.toString()
        binding.bluetoothBtn.setOnClickListener {
            bluetoothHandler.sendMessage(technic)
            dialog?.dismiss()
        }
        binding.broadcastBtn.setOnClickListener {
            val subscriberListDialog = SubscriberListDialog(object : SubscriberListCallback{
                override fun select(subscriber: Subscriber) {
                    targetFragmentCallback.onBroadcastButtonClick(subscriber.id, technic)
                }

            })
            subscriberListDialog.show(parentFragmentManager, "listDialog")
            dialog?.dismiss()
        }
        binding.deleteBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Кнопки пока не работают", Toast.LENGTH_SHORT).show()
            targetFragmentCallback.deleteTarget()
        }
        return binding.root
    }

    interface TargetFragmentCallback{
        fun onBroadcastButtonClick(destinationId: String, technic: Technic)
        fun deleteTarget()
    }
}
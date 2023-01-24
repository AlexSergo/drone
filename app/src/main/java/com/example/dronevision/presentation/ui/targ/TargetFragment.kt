package com.example.dronevision.presentation.ui.targ

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.App
import com.example.dronevision.databinding.FragmentTargBinding
import com.example.dronevision.presentation.delegates.BluetoothHandler
import com.example.dronevision.presentation.model.Subscriber
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.MainViewModel
import com.example.dronevision.presentation.ui.MainViewModelFactory
import com.example.dronevision.presentation.ui.subscribers.SubscriberListCallback
import com.example.dronevision.presentation.ui.subscribers.SubscriberListDialog
import com.example.dronevision.presentation.ui.subscribers.SubscribersType
import com.example.dronevision.utils.Device.toJson
import javax.inject.Inject


class TargetFragment(
    private val technic: Technic,
    private val targetFragmentCallback: TargetFragmentCallback
) : DialogFragment() {
    
    private lateinit var binding: FragmentTargBinding

    private lateinit var targetViewModel: TargetViewModel

    @Inject
    lateinit var targetViewModelFactory: TargetViewModelFactory
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewModel()

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
            val subscriberListDialog = SubscriberListDialog(object : SubscriberListCallback {
                override fun select(subscriber: Subscriber) {
                    targetFragmentCallback.onBroadcastButtonClick(subscriber.id, technic)
                }
                
            }, SubscribersType.Internet)
            subscriberListDialog.show(parentFragmentManager, "listDialog")
            dialog?.dismiss()
        }

        binding.telegramBtn.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, technic.toJson())
            sendIntent.type = "text/plain"
            sendIntent.setPackage("org.telegram.messenger")
            try {
                startActivity(sendIntent)
            } catch (ex: ActivityNotFoundException) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("На вашем устройстве нет Telegram!")
                    .setMessage("Для начала установите telegram")
                    .setPositiveButton("ОК") {
                            dialog, id ->  dialog.cancel()
                    }
                builder.create()
                builder.show()
            }
        }

        binding.deleteBtn.setOnClickListener {
            targetFragmentCallback.deleteTechnic()
            dialog?.dismiss()
        }

        binding.radioBtn.setOnClickListener {
            val subscriberListDialog = SubscriberListDialog(object : SubscriberListCallback {
                override fun select(subscriber: Subscriber) {
                    targetViewModel.sendMessage("172.20.12.195", technic.toJson())
                }

            }, SubscribersType.Radio)
            subscriberListDialog.show(parentFragmentManager, "listDialog")
        }
        return binding.root
    }

    private fun initViewModel() {
        (requireContext().applicationContext as App).appComponent.inject(this)
        targetViewModel =
            ViewModelProvider(this, targetViewModelFactory)[TargetViewModel::class.java]
    }
}
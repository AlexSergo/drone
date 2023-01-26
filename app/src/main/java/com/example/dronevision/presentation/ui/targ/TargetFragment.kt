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
import com.example.dronevision.utils.Device
import com.example.dronevision.utils.Device.toJson
import com.example.dronevision.utils.NGeoCalc
import javax.inject.Inject


class TargetFragment(
    private val technic: Technic,
    private val targetFragmentCallback: TargetFragmentCallback,
    private val altitude: Double
) : DialogFragment() {

    private lateinit var binding: FragmentTargBinding

    private lateinit var targetViewModel: TargetViewModel

    @Inject
    lateinit var targetViewModelFactory: TargetViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTargBinding.inflate(inflater, container, false)

        initViewModel()
        initText()
        setBluetoothOnClickListener()
        setBroadcastClickListener()
        setTelegramClickListener()
        setDeleteClickListener()
        setRadioClickListener()
        initLongClicks()

        return binding.root
    }

    private fun initLongClicks() {
        binding.heightText.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.heightValue.text.toString())
            Toast.makeText(
                requireContext(),
                "Значение " + binding.heightValue.text.toString() + "скопировано!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
        binding.heightValue.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.heightValue.text.toString())
            Toast.makeText(
                requireContext(),
                "Значение " + binding.heightValue.text.toString() + "скопировано!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
        binding.latitudeText.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.latitudeValue.text.toString())
            Toast.makeText(
                requireContext(),
                "Значение " + binding.latitudeValue.text.toString() + "скопировано!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
        binding.latitudeValue.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.latitudeValue.text.toString())
            Toast.makeText(
                requireContext(),
                "Значение " + binding.latitudeValue.text.toString() + "скопировано!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
        binding.longitudeText.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.longitudeValue.text.toString())
            Toast.makeText(
                requireContext(),
                "Значение " + binding.longitudeValue.text.toString() + "скопировано!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
        binding.longitudeValue.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.longitudeValue.text.toString())
            Toast.makeText(
                requireContext(),
                "Значение " + binding.longitudeValue.text.toString() + "скопировано!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
    }

    private fun setRadioClickListener() {
        // TODO: в разработке
/*        binding.radioBtn.setOnClickListener {
            val subscriberListDialog = SubscriberListDialog(object : SubscriberListCallback {
                override fun select(subscriber: Subscriber) {
                    targetViewModel.sendMessage("192.168.11.1", technic.toJson())
                }

            }, SubscribersType.Radio)
            subscriberListDialog.show(parentFragmentManager, "listDialog")
        }*/
    }

    private fun setDeleteClickListener() {
        binding.deleteBtn.setOnClickListener {
            targetFragmentCallback.deleteTechnic()
            dialog?.dismiss()
        }
    }

    private fun setTelegramClickListener() {
        binding.telegramBtn.setOnClickListener {
            if (checkDivision()) {
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
                        .setPositiveButton("ОК") { dialog, id ->
                            dialog.cancel()
                        }
                    builder.create()
                    builder.show()
                }
            }
        }
    }

    private fun setBroadcastClickListener() {
        binding.broadcastBtn.setOnClickListener {
            if (checkDivision()) {
                val subscriberListDialog = SubscriberListDialog(object : SubscriberListCallback {
                    override fun select(subscriber: Subscriber) {
                        targetFragmentCallback.onBroadcastButtonClick(subscriber.id, technic)
                    }

                }, SubscribersType.Internet)
                subscriberListDialog.show(parentFragmentManager, "listDialog")
                dialog?.dismiss()
            }
        }
    }

    private fun setBluetoothOnClickListener() {
        val bluetoothHandler = requireActivity() as BluetoothHandler
        bluetoothHandler.acceptBluetoothConnection()

        binding.bluetoothBtn.setOnClickListener {
            if (checkDivision()) {
                bluetoothHandler.sendMessage(technic)
                dialog?.dismiss()
            }
        }
    }

    private fun checkDivision(): Boolean {
        if (technic.division == null) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Укажите свое подразделение!")
                .setMessage("Укажите подразделение в настройках! (Пункт: Мои данные)")
                .setPositiveButton("ОК") { dialog, id ->
                    dialog.cancel()
                }
            builder.create()
            builder.show()
            return false
        }
        return true
    }

    private fun initText() {
        val x = doubleArrayOf(0.0)
        val y = doubleArrayOf(0.0)

        NGeoCalc().wgs84ToPlane(
            x, y,
            doubleArrayOf(0.0),
            NGeoCalc.degreesToRadians(technic.coordinates.x),
            NGeoCalc.degreesToRadians(technic.coordinates.y),
            0.0
        )

        binding.header.text = technic.technicTypes.name
        binding.latitudeValue.text = x[0].toInt().toString()
        binding.longitudeValue.text = y[0].toInt().toString()
        binding.divisionValue.text = technic.division
        binding.heightValue.text = altitude.toString()

    }

    private fun initViewModel() {
        (requireContext().applicationContext as App).appComponent.inject(this)
        targetViewModel =
            ViewModelProvider(this, targetViewModelFactory)[TargetViewModel::class.java]
    }
}
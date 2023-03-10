package com.example.dronevision.presentation.ui.targ

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.dronevision.App
import com.example.dronevision.R
import com.example.dronevision.databinding.FragmentTargBinding
import com.example.dronevision.presentation.delegates.BluetoothHandler
import com.example.dronevision.presentation.mapper.TechnicMapperUI
import com.example.dronevision.presentation.mapper.TechnicMapperUI.mapTechnicToText
import com.example.dronevision.presentation.model.Subscriber
import com.example.dronevision.presentation.model.Technic
import com.example.dronevision.presentation.ui.subscribers.SubscriberListCallback
import com.example.dronevision.presentation.ui.subscribers.SubscriberListDialog
import com.example.dronevision.presentation.ui.subscribers.SubscribersType
import com.example.dronevision.utils.Device
import com.example.dronevision.utils.NGeoCalc
import com.example.dronevision.utils.toFormat
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.util.*
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
        binding = FragmentTargBinding.inflate(inflater, container, false)

        initViewModel()
        initText()
        setBluetoothOnClickListener()
        setBroadcastClickListener()
        setTelegramClickListener()
        setDeleteClickListener()
        setArtgroupBtnOnClickListener()
        setImportAlpineOnClickListener()
        setRadioClickListener()
        initLongClicks()

        return binding.root
    }

    private fun initLongClicks() {
        binding.heightText.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.heightValue.text.toString())
            Toast.makeText(
                requireContext(),
                "???????????????? " + binding.heightValue.text.toString() + "??????????????????????!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
        binding.heightValue.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.heightValue.text.toString())
            Toast.makeText(
                requireContext(),
                "???????????????? " + binding.heightValue.text.toString() + "??????????????????????!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
        binding.latitudeText.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.latitudeValue.text.toString())
            Toast.makeText(
                requireContext(),
                "???????????????? " + binding.latitudeValue.text.toString() + "??????????????????????!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
        binding.latitudeValue.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.latitudeValue.text.toString())
            Toast.makeText(
                requireContext(),
                "???????????????? " + binding.latitudeValue.text.toString() + "??????????????????????!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
        binding.longitudeText.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.longitudeValue.text.toString())
            Toast.makeText(
                requireContext(),
                "???????????????? " + binding.longitudeValue.text.toString() + "??????????????????????!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
        binding.longitudeValue.setOnLongClickListener {
            Device.setClipboard(requireContext(), binding.longitudeValue.text.toString())
            Toast.makeText(
                requireContext(),
                "???????????????? " + binding.longitudeValue.text.toString() + "??????????????????????!",
                Toast.LENGTH_SHORT
            ).show()
            return@setOnLongClickListener true
        }
    }

    private fun setRadioClickListener() {
        // TODO: ?? ????????????????????
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
                sendIntent.putExtra(Intent.EXTRA_TEXT, mapTechnicToText(technic))
                sendIntent.type = "text/plain"
                sendIntent.setPackage("org.telegram.messenger")
                try {
                    startActivity(sendIntent)
                } catch (ex: ActivityNotFoundException) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("???? ?????????? ???????????????????? ?????? Telegram!")
                        .setMessage("?????? ???????????? ???????????????????? telegram")
                        .setPositiveButton("????") { dialog, id ->
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

    private fun setImportAlpineOnClickListener() {
        binding.importAlpineQuestBtn.setOnClickListener {

            val date = Calendar.getInstance().toFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File("$absolutePath/${technic.technicTypes.name}.csv")

            val csvPrinter = CSVPrinter(file.bufferedWriter(), CSVFormat.DEFAULT
                .withHeader("longitude_wgs84(deg)", "latitude_wgs84(deg)", "elevation_egm(m)", "name", "comment", "date"))

            csvPrinter.printRecord("${technic.coordinates.y}", "${technic.coordinates.x}",
                "${technic.coordinates.h}", technic.technicTypes.name, "", date)
            csvPrinter.flush()
            csvPrinter.close()
            dialog?.dismiss()
            Toast.makeText(context, getString(R.string.success_target_save, file.absolutePath), Toast.LENGTH_LONG).show()
        }
    }

    private fun checkDivision(): Boolean {
        if (technic.division == null) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("?????????????? ???????? ????????????????!")
                .setMessage("?????????????? ???????????????? ?? ????????????????????! (??????????: ?????? ????????????)")
                .setPositiveButton("????") { dialog, id ->
                    dialog.cancel()
                }
            builder.create()
            builder.show()
            return false
        }
        return true
    }

    private fun setArtgroupBtnOnClickListener() {
        binding.artgroupBtn.setOnClickListener {
            if (checkDivision()) {
                Device.setClipboard(requireContext(),
                    TechnicMapperUI.mapTechnicToTextForArtgroup(technic)
                )
                val sendIntent = Intent()
                sendIntent.setPackage("ru.niissu.artgroup")
                try {
                    startActivity(sendIntent)
                } catch (ex: ActivityNotFoundException) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("???? ?????????? ???????????????????? ?????? ?????????????????? ??????????????????????!")
                        .setMessage("?????? ???????????? ???????????????????? ?????????????????? ??????????????????????")
                        .setPositiveButton("????") { dialog, id ->
                            dialog.cancel()
                        }
                    builder.create()
                    builder.show()
                }
            }
        }
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
        binding.heightValue.text = technic.coordinates.h.toString()

    }

    private fun initViewModel() {
        (requireContext().applicationContext as App).appComponent.inject(this)
        targetViewModel =
            ViewModelProvider(this, targetViewModelFactory)[TargetViewModel::class.java]
    }
}
package com.example.dronevision.presentation.delegates

import android.content.Context
import android.content.DialogInterface
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.dronevision.databinding.FragmentOsmdroidBinding
import com.example.dronevision.utils.FileTools.createAppFolder
import org.osmdroid.tileprovider.modules.OfflineTileProvider
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver
import org.osmdroid.views.MapView
import java.io.File

class OfflineMapHandlerImpl: OfflineMapHandler {
    private val rootDirName = Environment.getExternalStorageDirectory().path
    private val dirName = "$rootDirName/Drone Vision/"
    
    override fun offlineMode(mapView: MapView, context: Context) {
        createAppFolder()
        openFile(mapView, context)
    }
    
    private fun openFile(
        mapView: MapView,
        context: Context,
    ) {
        val folder = File(dirName)
        val listOfFiles = folder.listFiles()!!
        
        if (listOfFiles.isNotEmpty()) {
            val offlineArray: MutableList<String> = ArrayList()
            for (file in listOfFiles) {
                val isFileMBTiles =
                    file.name.substringAfterLast('.', "") == "mbtiles"
                if (file.isFile && isFileMBTiles) {
                    offlineArray.add(file.name)
                }
            }
            if (offlineArray.size != 0) {
                val items = offlineArray.toTypedArray()
                val mapOfflineBuilder = AlertDialog.Builder(context)
                mapOfflineBuilder.setTitle("Файлы БД оффлайн карт" as CharSequence)
                mapOfflineBuilder.create()
                mapOfflineBuilder.setItems(items as Array<CharSequence>,
                    DialogInterface.OnClickListener { _, which ->
                        
                        val fileName: String = items[which]
                        val path =
                            Environment.getExternalStorageDirectory().path + "/Drone Vision/" + fileName
                        val exitFile = File(path)
                        try {
                            mapView.setTileProvider(
                                OfflineTileProvider(
                                    SimpleRegisterReceiver(context),
                                    arrayOf(exitFile)
                                )
                            )
                            mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                            mapView.setUseDataConnection(false)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }).show()
            } else Toast.makeText(
                context,
                "Файлы mbtiles не найдены в $dirName",
                Toast.LENGTH_LONG
            ).show()
        } else Toast.makeText(
            context,
            "Файлы не найдены в $dirName",
            Toast.LENGTH_LONG
        ).show()
    }
}
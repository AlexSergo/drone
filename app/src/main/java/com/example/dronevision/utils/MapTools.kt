package com.example.dronevision.utils

import android.content.Context
import androidx.core.util.Pair
import com.yandex.mapkit.geometry.Point
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object MapTools {
    
    fun angleBetween(a: Point, b: Point): Double {
        val lat1 = Math.toRadians(a.latitude)
        val lng1 = Math.toRadians(a.longitude)
        val lat2 = Math.toRadians(b.latitude)
        val deltaLong = Math.toRadians(b.longitude) - lng1
        return (Math.toDegrees(
            atan2(
                sin(deltaLong) * Math.cos(lat2),
                cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLong)
            )
        ) + 360.0) % 360.0
    }
    
    fun getOSMMapTile(context: Context?, mapView: MapView): OnlineTileSourceBase {
        mapView.setUseDataConnection(true)
        mapView.tileProvider = MapTileProviderBasic(context)
        val osmPref = arrayOf("a", "b", "c")
        val str = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        return object : OnlineTileSourceBase(
            "HOT",
            1,
            20,
            256,
            "*.png",
            arrayOf("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png")
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                val x = MapTileIndex.getX(pMapTileIndex)
                val y = MapTileIndex.getY(pMapTileIndex)
                val zoom = MapTileIndex.getZoom(pMapTileIndex)
                return str.replace("{s}", osmPref[Random().nextInt(3) + 0])
                    .replace("{z}", zoom.toString()).replace("{x}", x.toString())
                    .replace("{y}", y.toString())
            }
        }
    }
    
    fun getGoogleMapTile(
        context: Context?,
        mapView: MapView,
        googleType: Pair<String, String>
    ): OnlineTileSourceBase {
        val url: String = "https://mt{s}.google.com/vt/lyrs={t}&hl=ru&x={x}&y={y}&z={z}".replace(
            "{t}",
            googleType.second
        )
        mapView.setUseDataConnection(true)
        mapView.tileProvider = MapTileProviderBasic(context)
        return object :
            OnlineTileSourceBase(googleType.first, 1, 23, 256, ".png", arrayOf(url), "© Google") {
            override fun getTileURLString(pMapTileIndex: Long): String {
                val x = MapTileIndex.getX(pMapTileIndex)
                val y = MapTileIndex.getY(pMapTileIndex)
                return url.replace("{s}", (Random().nextInt(4) + 0).toString())
                    .replace("{z}", MapTileIndex.getZoom(pMapTileIndex).toString())
                    .replace("{x}", x.toString()).replace("{y}", y.toString())
            }
        }
    }
}
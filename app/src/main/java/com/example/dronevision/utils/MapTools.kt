package com.example.dronevision.utils

import android.content.Context
import androidx.core.util.Pair
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import java.util.*
import kotlin.math.*

object MapTools {
    private const val EARTH_RADIUS = 6378140.0
    
    fun angleBetween(a: GeoPoint, b: GeoPoint): Double {
        val lat1 = Math.toRadians(a.latitude)
        val lng1 = Math.toRadians(a.longitude)
        val lat2 = Math.toRadians(b.latitude)
        val deltaLong = Math.toRadians(b.longitude) - lng1
        return (Math.toDegrees(
            atan2(
                sin(deltaLong) * cos(lat2),
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
    
    fun getHereMapTile(context: Context?, mapView: MapView): OnlineTileSourceBase {
        val url =
            "http://{s}.aerial.maps.cit.api.here.com/maptile/2.1/maptile/newest/hybrid.day/{z}/{x}/{y}/256/png8?app_id=eAdkWGYRoc4RfxVo0Z4B&app_code=TrLJuXVK62IQk0vuXFzaig&lg=eng"
        
        mapView.setUseDataConnection(true)
        mapView.tileProvider = MapTileProviderBasic(context)
        return object :
            OnlineTileSourceBase(
                "Here hybrid day",
                0,
                19,
                256,
                ".png",
                arrayOf(url),
                "© Here Technologies"
            ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                val x = MapTileIndex.getX(pMapTileIndex)
                val y = MapTileIndex.getY(pMapTileIndex)
                return url.replace("{s}", (Random().nextInt(4) + 1).toString())
                    .replace("{z}", MapTileIndex.getZoom(pMapTileIndex).toString())
                    .replace("{x}", x.toString()).replace("{y}", y.toString())
            }
    
        }
    }
    
    fun distanceBetween(a: GeoPoint, b: GeoPoint): Double {
        val lat1 = Math.toRadians(a.latitude)
        val lng1 = Math.toRadians(a.longitude)
        val lat2 = Math.toRadians(b.latitude)
        val cordlen = sin((lat2 - lat1) / 2.0).pow(2.0) + cos(lat1) * cos(lat2) * sin(
            (Math.toRadians(
                b.longitude
            ) - lng1) / 2.0
        ).pow(2.0)
        return EARTH_RADIUS * atan2(sqrt(cordlen), sqrt(1.0 - cordlen)) * 2.0
    }
}
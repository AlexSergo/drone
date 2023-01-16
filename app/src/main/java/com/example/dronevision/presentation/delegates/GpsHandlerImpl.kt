package com.example.dronevision.presentation.delegates

import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class GpsHandlerImpl : GpsHandler {
    override fun checkGPS(fragmentActivity: FragmentActivity): Boolean {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(600)
            .setMaxUpdateDelayMillis(1000)
            .build()
    
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()
        val res = LocationServices.getSettingsClient(fragmentActivity)
            .checkLocationSettings(builder)
    
        res.addOnCompleteListener { task ->
            try {
                // when the GPS is on
                task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                // when the GPS is OFF
                e.printStackTrace()
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // here we send the request for enable the GPS
                        val resolveApiException = e as ResolvableApiException
                        resolveApiException.startResolutionForResult(fragmentActivity, 200)
                    } catch (sendIntentException: IntentSender.SendIntentException) {
                        sendIntentException.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // when the setting is unavailable
                        Toast.makeText(
                            fragmentActivity,
                            "GPS выключен, не удаётся получить доступ к местоположению",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        res.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    // Handle result in onActivityResult()
                    e.startResolutionForResult(fragmentActivity, 999)
                } catch (sendEx: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
            e.printStackTrace()
        }
    
        val locationManager = fragmentActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
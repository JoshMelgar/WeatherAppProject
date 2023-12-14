package me.joshmelgar.weatherapp.helpers

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationHelper {

    companion object {
        @SuppressLint("MissingPermission")
        fun getCurrentLocation(context: Context, onLocationReceived: (latitude: Double, longitude: Double) -> Unit) {
            val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        onLocationReceived(location.latitude, location.longitude)
                    }
                }
        }
    }
}

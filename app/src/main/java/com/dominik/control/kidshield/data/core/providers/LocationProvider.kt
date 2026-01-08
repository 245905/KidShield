package com.dominik.control.kidshield.data.core.providers

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationProvider(@ApplicationContext context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun fetchCurrentLocation(timeoutMs: Long = 10_000L): Location? {
        val cts = CancellationTokenSource()

        return try {
            withTimeout(timeoutMs) {
                suspendCancellableCoroutine<Location?> { cont ->
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        cts.token
                    )
                        .addOnSuccessListener { loc ->
                            cont.resume(loc)
                        }
                        .addOnFailureListener { ex ->
                            cont.resumeWithException(ex)
                        }

                    cont.invokeOnCancellation {
                        cts.cancel()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("dev", e.toString())
            null
        }
    }

}

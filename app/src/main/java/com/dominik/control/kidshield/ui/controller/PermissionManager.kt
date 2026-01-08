package com.dominik.control.kidshield.ui.controller

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PermissionStatus {
    GRANTED,
    DENIED,
    DENIED_PERMANENTLY
}

data class PermissionState(
    val fineLocation: PermissionStatus,
    val backgroundLocation: PermissionStatus,
    val activityRecognition: PermissionStatus,
    val usageStats: PermissionStatus,
    val notifications: PermissionStatus
) {
    val canTrackBackground: Boolean =
        fineLocation == PermissionStatus.GRANTED &&
                backgroundLocation == PermissionStatus.GRANTED

    val canReadAppUsage: Boolean =
        usageStats == PermissionStatus.GRANTED
}

class PermissionManager(
    private val host: ComponentActivity
) {

    private val _state = MutableStateFlow(readCurrentState())
    val state: StateFlow<PermissionState> = _state.asStateFlow()

    // ---------- launchers ----------

    private val foregroundLauncher =
        host.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            updateState()
        }

    private val backgroundLauncher =
        host.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            updateState()
        }

    private val activityLauncher =
        host.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            updateState()
        }

    private val notificationLauncher =
        host.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            updateState()
        }


    // ---------- public API ----------

    fun requestForegroundLocation() {
        foregroundLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun requestBackgroundLocation() {
        backgroundLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }

    fun requestActivityRecognition() {
        activityLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
    }

    fun requestPostNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activityLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    fun openUsageStatsSettings() {
        host.startActivity(
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    fun openAppSettings() {
        host.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", host.applicationContext.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    fun refresh() {
        updateState()
    }

    // ---------- internals ----------

    private fun updateState() {
        _state.value = readCurrentState()
    }

    private fun readCurrentState(): PermissionState {
        return PermissionState(
            fineLocation =
                permissionStatus(Manifest.permission.ACCESS_FINE_LOCATION),
            backgroundLocation =
                permissionStatus(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            activityRecognition =
                permissionStatus(Manifest.permission.ACTIVITY_RECOGNITION),
            usageStats = usageStatsStatus(),
            notifications = permissionStatus(Manifest.permission.ACTIVITY_RECOGNITION)
        )
    }

    private fun permissionStatus(permission: String): PermissionStatus {
        val granted = ContextCompat.checkSelfPermission(
            host,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) return PermissionStatus.GRANTED

        val canAskAgain =
            ActivityCompat.shouldShowRequestPermissionRationale(host, permission)

        return if (canAskAgain)
            PermissionStatus.DENIED
        else
            PermissionStatus.DENIED_PERMANENTLY
    }

    private fun usageStatsStatus(): PermissionStatus {
        return if (hasUsageStatsPermission(host)) {
            PermissionStatus.GRANTED
        } else {
            PermissionStatus.DENIED
        }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}

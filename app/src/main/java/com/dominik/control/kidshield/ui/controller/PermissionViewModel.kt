package com.dominik.control.kidshield.ui.controller

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel(assistedFactory = PermissionViewModel.Factory::class)
class PermissionViewModel @AssistedInject constructor(
    @Assisted val permissionManager: PermissionManager,
    @ApplicationContext private val context: Context
) : ViewModel()   {

    @AssistedFactory
    interface Factory {
        fun create(permissionManager: PermissionManager): PermissionViewModel
    }

    fun requestActivityRecognition() {
        permissionManager.requestActivityRecognition()
    }

    fun requestBackgroundLocation() {
        permissionManager.requestBackgroundLocation()
    }

    fun requestForegroundLocation() {
        permissionManager.requestForegroundLocation()
    }

    fun openUsageStatsSettings() {
        permissionManager.openUsageStatsSettings()
    }

    fun requestPostNotificationsSettings() {
        permissionManager.requestPostNotifications()
    }

    fun openAppSettings() {
        permissionManager.openAppSettings()
    }

    fun areAllPermissionsGranted(): Boolean {
        return permissionManager.state.value.canReadAppUsage
                && permissionManager.state.value.canTrackBackground
                && permissionManager.state.value.activityRecognition==PermissionStatus.GRANTED
    }

    fun isPermissionDeniedPermanently(): Boolean {
        return permissionManager.state.value.fineLocation==PermissionStatus.DENIED_PERMANENTLY
                && permissionManager.state.value.backgroundLocation==PermissionStatus.DENIED_PERMANENTLY
                && permissionManager.state.value.activityRecognition==PermissionStatus.DENIED_PERMANENTLY
                && permissionManager.state.value.usageStats==PermissionStatus.DENIED_PERMANENTLY
    }

    fun onStartFullControlClicked() {
        val state = permissionManager.state.value
        val started = TrackingController.startFullControlIfAllowed(context, state, startServiceFromUi = true)
        if (!started) {
            // emituj UI event (snackbar) - brak perms
        } else {
            // navigate home / update UI
        }
    }

}
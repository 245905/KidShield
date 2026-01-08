package com.dominik.control.kidshield.data.core.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dominik.control.kidshield.data.model.domain.PointEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import com.dominik.control.kidshield.data.repository.RouteRepository
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

object LocationProfiles {
    val MOVING = LocationRequest.Builder(10_000L)     // 10s      interval
        .setMinUpdateIntervalMillis(5_000L)                      // 5s       fastestInterval
        .setMaxUpdateDelayMillis(60_000L)                        // 1min     maxWaitTime
        .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        .setWaitForAccurateLocation(false)
        .build()

    val IDLE = LocationRequest.Builder(60_000L)       // 60s
        .setMinUpdateIntervalMillis(30_000L)                     // 30s
        .setMaxUpdateDelayMillis(120_000L)                       // 2min
        .setPriority(Priority.PRIORITY_LOW_POWER)
        .setWaitForAccurateLocation(false)
        .build()
}

@AndroidEntryPoint
class LocationForegroundService : Service() {

    @Inject
    lateinit var repository: RouteRepository

    private val locationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var currentProfile: LocationRequest = LocationProfiles.IDLE
    private var isRunning = false

    private val activityRecognitionClient: ActivityRecognitionClient by lazy {
        ActivityRecognition.getClient(applicationContext)
    }

    @Volatile
    private var lastLocationSpeed: Float? = null


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d("location", "onCreate: service")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("location", "onStartCommand: service")
        Log.d("location", "onStartCommand: "+intent.toString()+flags.toString()+startId.toString())

        when (intent?.action) {
            ACTION_START -> startForegroundAndUpdates()
            ACTION_STOP -> myStopSelf()
            ACTION_PAUSE -> pauseUpdates()
            ACTION_RESUME -> resumeUpdates()
            ACTION_SWITCH_PROFILE -> toggleProfile()
            ACTION_ACTIVITY_UPDATE -> {
                val type = intent.getIntExtra("activity_type", DetectedActivity.UNKNOWN)
                val confidence = intent.getIntExtra("activity_confidence", 0)
                onActivityDetected(type, confidence)
            }
            else -> {
                if (!isRunning) startForegroundAndUpdates()
            }
        }
        return START_STICKY
    }

    private fun startForegroundAndUpdates() {
        Log.d("location", "startForegroundAndUpdates: service")
        Log.d("location", "startForegroundAndUpdates: "+isRunning.toString())
        Log.d("location", "startForegroundAndUpdates: "+currentProfile.toString())

        if (isRunning) return
        isRunning = true

        startForeground(NOTIF_ID, buildNotification())
        requestLocationUpdates(currentProfile)
        startActivityRecognition()
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        Log.d("location", "onDestroy: service")

        stopActivityRecognition()
        serviceScope.cancel()
        locationProvider.removeLocationUpdates(locationCallback)
    }

    private fun myStopSelf() {
        Log.d("location", "myStopSelf: service")

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


    //--------------------LOCATION--------------------

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            Log.d("location", "onLocationResult: service")

            val now = System.currentTimeMillis()
            result.locations.forEach { loc ->
                lastLocationSpeed = if (loc.hasSpeed()) loc.speed else lastLocationSpeed
                serviceScope.launch {
                    handleLocation(loc, now)
                }
            }
        }
    }

    private fun pauseUpdates() {
        if (!isRunning) return
        locationProvider.removeLocationUpdates(locationCallback)
    }

    private fun resumeUpdates() {
        if (!isRunning) return
        requestLocationUpdates(currentProfile)
    }

    private fun toggleProfile() {
        Log.d("location", "toggleProfile: ")
        currentProfile = if (currentProfile == LocationProfiles.MOVING) {
            LocationProfiles.IDLE
        } else {
            LocationProfiles.MOVING
        }

        locationProvider.removeLocationUpdates(locationCallback)
        requestLocationUpdates(currentProfile)
    }

    private fun requestLocationUpdates(request: LocationRequest) {
        Log.d("location", "requestLocationUpdates: service")
        try {
            locationProvider.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            Log.d("location", "requestLocationUpdates: ex"+e.message+e.toString())
            myStopSelf()
        }
    }

    private suspend fun handleLocation(loc: Location, now: Long) {
        if (!isLocationValid(loc)) return

        val p = PointEntity(lat = loc.latitude, lon = loc.longitude, speed = loc.speed, fixTime = loc.time, receivedTime = now, status = UploadStatusType.PENDING)
        repository.insertPoint(p)

    }

    private fun isLocationValid(loc: Location): Boolean {
        if (loc.accuracy <= 0f) return false
        if (loc.latitude == 0.0 && loc.longitude == 0.0) return false
        return true
    }


    //--------------------ACTIVITY RECOGNITION--------------------

    private fun startActivityRecognition() {
        try {
            val pi = PendingIntent.getBroadcast(
                applicationContext,
                ACTIVITY_PENDING_INTENT_REQUEST,
                Intent(applicationContext, ActivityUpdatesReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            activityRecognitionClient.requestActivityUpdates(60_000L, pi)
                .addOnFailureListener {
                    Log.d("location", "startActivityRecognition: couldnt request activity update")
                }
        } catch (e: SecurityException) {
            Log.d("location", "startActivityRecognition: lack of permission")
        }
    }

    private fun stopActivityRecognition() {
        try {
            val pi = PendingIntent.getBroadcast(
                applicationContext,
                ACTIVITY_PENDING_INTENT_REQUEST,
                Intent(applicationContext, ActivityUpdatesReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            activityRecognitionClient.removeActivityUpdates(pi)
        } catch (e: SecurityException) {
            Log.d("location", "startActivityRecognition: lack of permission")
        }
    }

    private fun onActivityDetected(type: Int, confidence: Int) {
        Log.d("location", "onActivityDetected: "+confidence.toString())
        if (confidence >= ACTIVITY_CONFIDENCE_THRESHOLD) {
            when (type) {
                DetectedActivity.IN_VEHICLE, DetectedActivity.ON_BICYCLE -> switchToMovingIfNeeded()
                DetectedActivity.ON_FOOT, DetectedActivity.WALKING, DetectedActivity.RUNNING -> switchToMovingIfNeeded()
                DetectedActivity.STILL, DetectedActivity.TILTING -> switchToIdleIfNeeded()
                else -> {  }
            }
        } else {
            val speed = lastLocationSpeed ?: return
            if (speed >= SPEED_MOVING_THRESHOLD_MPS) switchToMovingIfNeeded() else switchToIdleIfNeeded()
        }
    }

    private fun switchToMovingIfNeeded() {
        if (currentProfile != LocationProfiles.MOVING) {
            currentProfile = LocationProfiles.MOVING
            // restart updates with new profile
            locationProvider.removeLocationUpdates(locationCallback)
            requestLocationUpdates(currentProfile)
        }
    }

    private fun switchToIdleIfNeeded() {
        if (currentProfile != LocationProfiles.IDLE) {
            currentProfile = LocationProfiles.IDLE
            locationProvider.removeLocationUpdates(locationCallback)
            requestLocationUpdates(currentProfile)
        }
    }


    //--------------------NOTIFICATIONS--------------------

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("location", "Channels: " + notificationManager.notificationChannels.joinToString())
        val channel = NotificationChannel(NOTIF_CHANNEL_ID, "Tracking", NotificationManager.IMPORTANCE_LOW)
        channel.description = "Location tracking"
        notificationManager.createNotificationChannel(channel)
        Log.d("location", "Channels: " + notificationManager.notificationChannels.joinToString())
    }

    private fun buildNotification(): Notification {
        val stopIntent = getService(
            this,
            0,
            Intent(this, LocationForegroundService::class.java).apply { action = ACTION_STOP },
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        val toggleIntent = getService(
            this,
            1,
            Intent(this, LocationForegroundService::class.java).apply { action = ACTION_SWITCH_PROFILE },
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setContentTitle("Location tracking")
            .setContentText("Follow in background active")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(android.R.drawable.ic_media_pause, "Switch profile", toggleIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopIntent)
            .build()
    }

    companion object {
        const val ACTION_START = "com.dominik.control.kidshield.location.action.START"
        const val ACTION_STOP = "com.dominik.control.kidshield.location.action.STOP"
        const val ACTION_PAUSE = "com.dominik.control.kidshield.location.action.PAUSE"
        const val ACTION_RESUME = "com.dominik.control.kidshield.location.action.RESUME"
        const val ACTION_SWITCH_PROFILE = "com.dominik.control.kidshield.location.action.SWITCH_PROFILE"
        const val ACTION_ACTIVITY_UPDATE = "com.dominik.control.kidshield.location.action.ACTIVITY_UPDATE"

        const val SPEED_MOVING_THRESHOLD_MPS = 1.1
        const val ACTIVITY_CONFIDENCE_THRESHOLD = 75
        const val ACTIVITY_PENDING_INTENT_REQUEST = 3456
        const val NOTIF_CHANNEL_ID = "tracking_channel"
        const val NOTIF_ID = 23456
    }
}

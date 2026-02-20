package com.dominik.control.kidshield.data.core.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dominik.control.kidshield.data.core.providers.SensorProvider
import com.dominik.control.kidshield.data.repository.SensorInfoRepository
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

@AndroidEntryPoint
class SensorService : Service() {

    @Inject
    lateinit var repository: SensorInfoRepository

    @Inject
    lateinit var provider: SensorProvider

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d("sensor", "onCreate: service")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("sensor", "onStartCommand: service")
        Log.d("sensor", "onStartCommand: "+intent.toString()+flags.toString()+startId.toString())

        when (intent?.action) {
            ACTION_START -> startForegroundAndUpdates()
            ACTION_STOP -> myStopSelf()
            ACTION_PAUSE -> pauseUpdates()
            ACTION_RESUME -> resumeUpdates()
            else -> {
                if (!isRunning) startForegroundAndUpdates()
            }
        }
        return START_STICKY
    }

    private fun startForegroundAndUpdates() {
        Log.d("location", "startForegroundAndUpdates: service")
        Log.d("location", "startForegroundAndUpdates: "+isRunning.toString())

        if (isRunning) return
        isRunning = true

        startForeground(NOTIF_ID, buildNotification())
        provider.start()
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        Log.d("sensor", "onDestroy: service")
        super.onDestroy()

        serviceScope.cancel()
        provider.stop()
    }

    private fun myStopSelf() {
        Log.d("location", "myStopSelf: service")

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun pauseUpdates() {
        if (!isRunning) return
        provider.stop()
    }

    private fun resumeUpdates() {
        if (!isRunning) return
        provider.start()
    }

    //--------------------NOTIFICATIONS--------------------

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("sensor", "Channels: " + notificationManager.notificationChannels.joinToString())
        val channel = NotificationChannel(NOTIF_CHANNEL_ID, "Sensors", NotificationManager.IMPORTANCE_LOW)
        channel.description = "Sensors monitoring"
        notificationManager.createNotificationChannel(channel)
        Log.d("sensor", "Channels: " + notificationManager.notificationChannels.joinToString())
    }

    private fun buildNotification(): Notification {
        val stopIntent = getService(
            this,
            0,
            Intent(this, SensorService::class.java).apply { action = ACTION_STOP },
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        val toggleIntent = getService(
            this,
            1,
            Intent(this, SensorService::class.java).apply { action = ACTION_SWITCH_PROFILE },
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
            .setContentTitle("Sensor monitoring")
            .setContentText("Follow in background active")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(android.R.drawable.ic_media_pause, "Switch profile", toggleIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopIntent)
            .build()
    }

    companion object {
        const val ACTION_START = "com.dominik.control.kidshield.sensor.action.START"
        const val ACTION_STOP = "com.dominik.control.kidshield.sensor.action.STOP"
        const val ACTION_PAUSE = "com.dominik.control.kidshield.sensor.action.PAUSE"
        const val ACTION_RESUME = "com.dominik.control.kidshield.sensor.action.RESUME"
        const val ACTION_SWITCH_PROFILE = "com.dominik.control.kidshield.sensor.action.SWITCH_PROFILE"

        const val NOTIF_CHANNEL_ID = "sensor_channel"
        const val NOTIF_ID = 33456
    }
}

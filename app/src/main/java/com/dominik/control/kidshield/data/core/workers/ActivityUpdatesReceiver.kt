package com.dominik.control.kidshield.data.core.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityRecognitionResult

class ActivityUpdatesReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val result = ActivityRecognitionResult.extractResult(intent) ?: return
        Log.d("location", "onReceive: "+result)

        // select most probable activity
        val mostProbable = result.mostProbableActivity
        val type = mostProbable.type
        val confidence = mostProbable.confidence

        val forward = Intent(context, LocationForegroundService::class.java).apply {
            action = LocationForegroundService.ACTION_ACTIVITY_UPDATE
            putExtra("activity_type", type)
            putExtra("activity_confidence", confidence)
        }
        ContextCompat.startForegroundService(context, forward)
    }
}

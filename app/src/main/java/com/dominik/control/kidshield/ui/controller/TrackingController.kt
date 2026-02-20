package com.dominik.control.kidshield.ui.controller

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.dominik.control.kidshield.data.core.workers.GatherAppInfoWorker
import com.dominik.control.kidshield.data.core.workers.GatherHourlyStatsWorker
import com.dominik.control.kidshield.data.core.workers.GatherUsageStatsWorker
import com.dominik.control.kidshield.data.core.workers.LocationForegroundService
import com.dominik.control.kidshield.data.core.workers.SensorService
import com.dominik.control.kidshield.data.core.workers.UploadSigMotionWorker
import com.dominik.control.kidshield.data.core.workers.UploadStepCountWorker
import com.dominik.control.kidshield.data.core.workers.UploadAppInfoWorker
import com.dominik.control.kidshield.data.core.workers.UploadHourlyStatsWorker
import com.dominik.control.kidshield.data.core.workers.UploadRouteWorker
import com.dominik.control.kidshield.data.core.workers.UploadUsageStatsWorker
import com.dominik.control.kidshield.utils.isServiceRunning
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

object TrackingController {

    private const val W_GATHER_APPS = "gather_apps_periodic"
    private const val W_UPLOAD_APPS = "upload_apps_periodic"
    private const val W_GATHER_USAGE = "gather_usage_periodic"
    private const val W_UPLOAD_USAGE = "upload_usage_periodic"
    private const val W_GATHER_HOURLY = "gather_hourly_periodic"
    private const val W_UPLOAD_HOURLY = "upload_hourly_periodic"
    private const val W_UPLOAD_ROUTES = "upload_routes_periodic"
    private const val W_UPLOAD_STEPS = "upload_steps_periodic"
    private const val W_UPLOAD_MOTION = "upload_motion_periodic"
    private const val W_WATCHDOG = "tracking_watchdog"

    fun startFullControlIfAllowed(
        context: Context,
        permissionState: PermissionState,
        startServiceFromUi: Boolean = true
    ): Boolean {
        if (!permissionState.canTrackBackground) {
            return false
        }

        if (startServiceFromUi) {
            val startIntent = Intent(context, LocationForegroundService::class.java)
                .setAction(LocationForegroundService.ACTION_START)
            ContextCompat.startForegroundService(context, startIntent)

            val startIntent2 = Intent(context, SensorService::class.java)
                .setAction(SensorService.ACTION_START)
            ContextCompat.startForegroundService(context, startIntent2)
        }

        scheduleAllWorkers(context)
        enqueueInitialOneTimeJobs(context)
        return true
    }

    fun stopFullControl(context: Context) {
        // Stop the foreground service
        val stop = Intent(context, LocationForegroundService::class.java).apply {
            action = LocationForegroundService.ACTION_STOP
        }
        ContextCompat.startForegroundService(context, stop)

        val stop2 = Intent(context, SensorService::class.java).apply {
            action = SensorService.ACTION_STOP
        }
        ContextCompat.startForegroundService(context, stop2)

        // Cancel scheduled periodic workers
        val wm = WorkManager.getInstance(context)
        wm.cancelUniqueWork(W_GATHER_APPS)
        wm.cancelUniqueWork(W_UPLOAD_APPS)
        wm.cancelUniqueWork(W_GATHER_USAGE)
        wm.cancelUniqueWork(W_UPLOAD_USAGE)
        wm.cancelUniqueWork(W_GATHER_HOURLY)
        wm.cancelUniqueWork(W_UPLOAD_HOURLY)
        wm.cancelUniqueWork(W_UPLOAD_ROUTES)
        wm.cancelUniqueWork(W_UPLOAD_STEPS)
        wm.cancelUniqueWork(W_UPLOAD_MOTION)
        wm.cancelUniqueWork(W_WATCHDOG)
    }

    private fun scheduleAllWorkers(context: Context) {
        val wm = WorkManager.getInstance(context)

        val networkConstraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // app info
        val gatherAppInfos = PeriodicWorkRequestBuilder<GatherAppInfoWorker>(12, TimeUnit.HOURS)
            .build()
        wm.enqueueUniquePeriodicWork(W_GATHER_APPS, ExistingPeriodicWorkPolicy.KEEP, gatherAppInfos)

        val uploadAppInfos = PeriodicWorkRequestBuilder<UploadAppInfoWorker>(6, TimeUnit.HOURS)
            .setConstraints(networkConstraint)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(W_UPLOAD_APPS, ExistingPeriodicWorkPolicy.KEEP, uploadAppInfos)

        // usage stats
        val gatherUsageStats = PeriodicWorkRequestBuilder<GatherUsageStatsWorker>(6, TimeUnit.HOURS)
            .build()
        wm.enqueueUniquePeriodicWork(W_GATHER_USAGE, ExistingPeriodicWorkPolicy.KEEP, gatherUsageStats)

        val uploadUsageStats = PeriodicWorkRequestBuilder<UploadUsageStatsWorker>(3, TimeUnit.HOURS)
            .setConstraints(networkConstraint)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(W_UPLOAD_USAGE, ExistingPeriodicWorkPolicy.KEEP, uploadUsageStats)

        // hourly stats
        val gatherHourlyStats = PeriodicWorkRequestBuilder<GatherHourlyStatsWorker>(3, TimeUnit.HOURS)
            .build()
        wm.enqueueUniquePeriodicWork(W_GATHER_HOURLY, ExistingPeriodicWorkPolicy.KEEP, gatherHourlyStats)

        val uploadHourlyStats = PeriodicWorkRequestBuilder<UploadHourlyStatsWorker>(1, TimeUnit.HOURS)
            .setConstraints(networkConstraint)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(W_UPLOAD_HOURLY, ExistingPeriodicWorkPolicy.KEEP, uploadHourlyStats)

        // route
        val uploadRoutes = PeriodicWorkRequestBuilder<UploadRouteWorker>(1, TimeUnit.HOURS)
            .setConstraints(networkConstraint)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(W_UPLOAD_ROUTES, ExistingPeriodicWorkPolicy.KEEP, uploadRoutes)

        // steps
        val uploadSteps = PeriodicWorkRequestBuilder<UploadStepCountWorker>(1, TimeUnit.HOURS)
            .setConstraints(networkConstraint)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(W_UPLOAD_STEPS, ExistingPeriodicWorkPolicy.KEEP, uploadSteps)

        // motion
        val uploadMotion = PeriodicWorkRequestBuilder<UploadSigMotionWorker>(1, TimeUnit.HOURS)
            .setConstraints(networkConstraint)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        wm.enqueueUniquePeriodicWork(W_UPLOAD_MOTION, ExistingPeriodicWorkPolicy.KEEP, uploadMotion)

        // 4) Optional: watchdog (every 15 minutes) — used to detect and attempt to re-start tracking if needed.
        // NOTE: restarting a location FGS from background may be blocked on Android 15+, so watchdog should prefer
        // to notify user or schedule a Foreground Worker that can request user attention.
//        val watchdog = PeriodicWorkRequestBuilder<TrackingWatchdogWorker>(15, TimeUnit.MINUTES)
//            .setConstraints(
//                Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
//                    .build()
//            )
//            .build()
//        wm.enqueueUniquePeriodicWork(W_WATCHDOG, ExistingPeriodicWorkPolicy.KEEP, watchdog)
    }

    private fun enqueueInitialOneTimeJobs(context: Context) {
        val wm = WorkManager.getInstance(context)

        val gatherAppInfo = OneTimeWorkRequestBuilder<GatherAppInfoWorker>().build()
        val uploadAppInfo = OneTimeWorkRequestBuilder<UploadAppInfoWorker>().build()
        wm.beginWith(gatherAppInfo).then(uploadAppInfo).enqueue()

        val gatherUsageStats = OneTimeWorkRequestBuilder<GatherUsageStatsWorker>().build()
        val uploadUsageStats = OneTimeWorkRequestBuilder<UploadUsageStatsWorker>().build()
        wm.beginWith(gatherUsageStats).then(uploadUsageStats).enqueue()

        val gatherHourlyStats = OneTimeWorkRequestBuilder<GatherHourlyStatsWorker>().build()
        val uploadHourlyStats = OneTimeWorkRequestBuilder<UploadHourlyStatsWorker>().build()
        wm.beginWith(gatherHourlyStats).then(uploadHourlyStats).enqueue()
    }
}

@HiltWorker
class TrackingWatchdogWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val ctx = applicationContext

        // odczytaj flagę w DataStore / SharedPreferences czy tracking był włączony
        val trackingEnabled = /* read your DataStore flag */ true

        if (!trackingEnabled) return Result.success()

        // sprawdź czy service działa
        val running = ctx.isServiceRunning(LocationForegroundService::class.java)
        if (!running) {
            // Nie próbuj bezwarunkowo odpalać FGS typu location z backgroundu (Android 15+ blokuje).
            // Lepiej: wyślij powiadomienie do użytkownika z CTA „Przywróć śledzenie” które otwiera appkę.
            showBringAppToFrontNotification(ctx)
        }
        return Result.success()
    }

    private fun showBringAppToFrontNotification(ctx: Context) {
        // Stwórz notyfikację, która po kliknięciu otworzy Activity i tam restartuje service.
    }
}


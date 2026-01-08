package com.dominik.control.kidshield.data.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dominik.control.kidshield.data.repository.RouteRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadRouteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: RouteRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val pending = repository.getPendingPoints()
        if(pending.isEmpty()) return Result.success()

        repository.updateUploadingPoints(pending)

        return try {
            val resp = repository.uploadData(pending)
            if (resp.isSuccess) {
                repository.updateUploadedPoints(pending)
                Result.success()
            } else {
                repository.updatePendingPoints(pending)
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

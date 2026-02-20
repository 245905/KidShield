package com.dominik.control.kidshield.data.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dominik.control.kidshield.data.repository.SigMotionRepository
import com.dominik.control.kidshield.data.repository.StepCountRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadStepCountWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: StepCountRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val pending = repository.getPendingStepCounts()
        if(pending.isEmpty()) return Result.success()

        repository.updateUploadingStepCounts(pending)

        return try {
            val resp = repository.uploadData(pending)
            if (resp.isSuccess) {
                repository.updateUploadedStepCounts(pending)
                Result.success()
            } else {
                repository.updatePendingStepCounts(pending)
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

@HiltWorker
class UploadSigMotionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: SigMotionRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val pending = repository.getPendingSigMotions()
        if(pending.isEmpty()) return Result.success()

        repository.updateUploadingSigMotions(pending)

        return try {
            val resp = repository.uploadData(pending)
            if (resp.isSuccess) {
                repository.updateUploadedSigMotions(pending)
                Result.success()
            } else {
                repository.updatePendingSigMotions(pending)
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

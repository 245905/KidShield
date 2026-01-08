package com.dominik.control.kidshield.data.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dominik.control.kidshield.data.core.providers.AppInfoProvider
import com.dominik.control.kidshield.data.repository.AppInfoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadAppInfoWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: AppInfoRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val pending = repository.getPendingAppInfos()
        if(pending.isEmpty()) return Result.success()

        repository.updateUploadingAppInfos(pending)

        return try {
            val resp = repository.uploadData(pending)
            if (resp.isSuccess) {
                repository.updateUploadedAppInfos(pending)
                Result.success()
            } else {
                repository.updatePendingAppInfos(pending)
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

@HiltWorker
class GatherAppInfoWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: AppInfoRepository,
    private val provider: AppInfoProvider
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val data = provider.fetchInstalledApps()
        if(data.isEmpty()) return Result.retry()
        repository.deleteAllAppInfos()
        repository.insertAppInfos(data)
        return Result.success()
    }
}

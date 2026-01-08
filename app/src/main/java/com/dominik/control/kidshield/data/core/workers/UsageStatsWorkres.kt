package com.dominik.control.kidshield.data.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dominik.control.kidshield.data.core.providers.AppTimeProvider
import com.dominik.control.kidshield.data.model.domain.UploadTableType
import com.dominik.control.kidshield.data.model.domain.UploadedStatsEntity
import com.dominik.control.kidshield.data.repository.UploadedStatsRepository
import com.dominik.control.kidshield.data.repository.UsageStatsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@HiltWorker
class UploadUsageStatsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: UsageStatsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val pending = repository.getPendingUsageStats()
        if(pending.isEmpty()) return Result.success()

        repository.updateUploadingUsageStats(pending)

        return try {
            val resp = repository.uploadData(pending)
            if (resp.isSuccess) {
                repository.updateUploadedUsageStats(pending)
                Result.success()
            } else {
                repository.updatePendingUsageStats(pending)
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

@HiltWorker
class GatherUsageStatsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: UsageStatsRepository,
    private val statusRepository: UploadedStatsRepository,
    private val provider: AppTimeProvider
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val localDate = LocalDate.now().minusDays(1)
        val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val uploaded = statusRepository.getUploadedStatsByDateAndByTableType(date, UploadTableType.USAGE)
        if(uploaded != null){
            val data = provider.fetchUsageStats(true)
            if(data.isNotEmpty()) {
                statusRepository.insertUploadedStats(
                    UploadedStatsEntity(
                        date = date,
                        tableType = UploadTableType.USAGE
                    )
                )
                repository.replaceUsageStats(data, date)
            }
        }

        val data = provider.fetchUsageStats()
        if(data.isEmpty()) return Result.retry()
        repository.replaceUsageStats(data, date)
        return Result.success()
    }
}

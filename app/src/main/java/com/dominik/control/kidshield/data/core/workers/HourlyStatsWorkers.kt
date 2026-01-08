package com.dominik.control.kidshield.data.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dominik.control.kidshield.data.core.providers.AppTimeProvider
import com.dominik.control.kidshield.data.model.domain.UploadTableType
import com.dominik.control.kidshield.data.model.domain.UploadedStatsEntity
import com.dominik.control.kidshield.data.repository.HourlyStatsRepository
import com.dominik.control.kidshield.data.repository.UploadedStatsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@HiltWorker
class UploadHourlyStatsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: HourlyStatsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val pending = repository.getPendingHourlyStats()
        if(pending.isEmpty()) return Result.success()

        repository.updateUploadingHourlyStats(pending)

        return try {
            val resp = repository.uploadData(pending)
            println(resp)
            if (resp.isSuccess) {
                repository.updateUploadedHourlyStats(pending)
                Result.success()
            } else {
                repository.updatePendingHourlyStats(pending)
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

@HiltWorker
class GatherHourlyStatsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: HourlyStatsRepository,
    private val statusRepository: UploadedStatsRepository,
    private val provider: AppTimeProvider
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val localDate = LocalDate.now().minusDays(1)
        val date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val uploaded = statusRepository.getUploadedStatsByDateAndByTableType(date, UploadTableType.USAGE)
        if(uploaded != null){
            val data = provider.fetchUsageEvents(previousDay = true)
            if(data.isNotEmpty()) {
                statusRepository.insertUploadedStats(
                    UploadedStatsEntity(
                        date = date,
                        tableType = UploadTableType.USAGE
                    )
                )
                repository.replaceHourlyStats(data, date)
            }
        }

        val data = provider.fetchUsageEvents(previousDay = true)
        if(data.isEmpty()) return Result.retry()
        repository.replaceHourlyStats(data, date)
        return Result.success()
    }
}

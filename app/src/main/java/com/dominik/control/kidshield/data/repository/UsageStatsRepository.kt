package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.dao.UsageStatsDao
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import com.dominik.control.kidshield.data.model.domain.UsageStatsEntity
import com.dominik.control.kidshield.data.remote.datasource.UsageStatsRemoteDataSource
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

interface UsageStatsRepository {
    suspend fun uploadData(data: List<UsageStatsEntity>): Result<Unit>
    suspend fun getAllUsageStats(): List<UsageStatsEntity>
    suspend fun insertUsageStats(data: List<UsageStatsEntity>): List<Long>
    suspend fun deleteUsageStats(data: List<UsageStatsEntity>): Int
    suspend fun replaceUsageStats(data: List<UsageStatsEntity>, date: Date): List<Long>
    suspend fun getPendingUsageStats(): List<UsageStatsEntity>
    suspend fun updateUploadedUsageStats(data: List<UsageStatsEntity>): Int
    suspend fun updateUploadingUsageStats(data: List<UsageStatsEntity>): Int
    suspend fun updatePendingUsageStats(data: List<UsageStatsEntity>): Int
}

class UsageStatsRepositoryImpl @Inject constructor(
    private val remote: UsageStatsRemoteDataSource,
    private val dao: UsageStatsDao,              // Room
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UsageStatsRepository {

    override suspend fun uploadData(data: List<UsageStatsEntity>): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                remote.uploadData(data)
            }
        }

    override suspend fun getAllUsageStats(): List<UsageStatsEntity> {
        return dao.getAllUsageStats()
    }

    override suspend fun insertUsageStats(data: List<UsageStatsEntity>): List<Long> {
        return dao.insertUsageStats(data)
    }

    override suspend fun deleteUsageStats(data: List<UsageStatsEntity>): Int {
        return dao.deleteUsageStats(data)
    }

    override suspend fun replaceUsageStats(data: List<UsageStatsEntity>, date: Date): List<Long> {
        dao.deleteByDate(date)
        return dao.insertUsageStats(data)
    }

    override suspend fun getPendingUsageStats(): List<UsageStatsEntity> {
        return dao.getUsageStatsByStatus(UploadStatusType.PENDING)
    }

    override suspend fun updateUploadedUsageStats(data: List<UsageStatsEntity>): Int {
        val uploaded = data.map { it.copy(status = UploadStatusType.UPLOADED) }
        return dao.updateUsageStats(uploaded)
    }

    override suspend fun updateUploadingUsageStats(data: List<UsageStatsEntity>): Int {
        val uploading = data.map { it.copy(status = UploadStatusType.UPLOADED) }
        return dao.updateUsageStats(uploading)
    }

    override suspend fun updatePendingUsageStats(data: List<UsageStatsEntity>): Int {
        val pending = data.map { it.copy(status = UploadStatusType.PENDING) }
        return dao.updateUsageStats(pending)
    }
}

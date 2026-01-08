package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.dao.HourlyStatsDao
import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import com.dominik.control.kidshield.data.remote.datasource.HourlyStatsRemoteDataSource
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

interface HourlyStatsRepository {
    suspend fun uploadData(data: List<HourlyStatsEntity>): Result<Unit>
    suspend fun getAllHourlyStats(): List<HourlyStatsEntity>
    suspend fun insertHourlyStats(data: List<HourlyStatsEntity>): List<Long>
    suspend fun deleteHourlyStats(data: List<HourlyStatsEntity>): Int
    suspend fun replaceHourlyStats(data: List<HourlyStatsEntity>, date: Date): List<Long>
    suspend fun getPendingHourlyStats(): List<HourlyStatsEntity>
    suspend fun updateUploadedHourlyStats(data: List<HourlyStatsEntity>): Int
    suspend fun updateUploadingHourlyStats(data: List<HourlyStatsEntity>): Int
    suspend fun updatePendingHourlyStats(data: List<HourlyStatsEntity>): Int
}

class HourlyStatsRepositoryImpl @Inject constructor(
    private val remote: HourlyStatsRemoteDataSource,
    private val dao: HourlyStatsDao,              // Room
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HourlyStatsRepository {

    override suspend fun uploadData(data: List<HourlyStatsEntity>): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                remote.uploadData(data)
            }
        }

    override suspend fun getAllHourlyStats(): List<HourlyStatsEntity> {
        return dao.getAllHourlyStats()
    }

    override suspend fun insertHourlyStats(data: List<HourlyStatsEntity>): List<Long> {
        return dao.insertHourlyStats(data)
    }

    override suspend fun deleteHourlyStats(data: List<HourlyStatsEntity>): Int {
        return dao.deleteHourlyStats(data)
    }

    override suspend fun replaceHourlyStats(data: List<HourlyStatsEntity>, date: Date): List<Long> {
        dao.deleteByDate(date)
        return dao.insertHourlyStats(data)
    }

    override suspend fun getPendingHourlyStats(): List<HourlyStatsEntity> {
        return dao.getHourlyStatsByStatus(UploadStatusType.PENDING)
    }

    override suspend fun updateUploadedHourlyStats(data: List<HourlyStatsEntity>): Int {
        val uploaded = data.map { it.copy(status = UploadStatusType.UPLOADED) }
        return dao.updateHourlyStats(uploaded)
    }

    override suspend fun updateUploadingHourlyStats(data: List<HourlyStatsEntity>): Int {
        val uploading = data.map { it.copy(status = UploadStatusType.UPLOADED) }
        return dao.updateHourlyStats(uploading)
    }

    override suspend fun updatePendingHourlyStats(data: List<HourlyStatsEntity>): Int {
        val pending = data.map { it.copy(status = UploadStatusType.PENDING) }
        return dao.updateHourlyStats(pending)
    }
}

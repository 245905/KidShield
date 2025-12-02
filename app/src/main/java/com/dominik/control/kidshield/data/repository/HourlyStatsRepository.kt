package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.dao.HourlyStatsDao
import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
import com.dominik.control.kidshield.data.remote.datasource.HourlyStatsRemoteDataSource
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface HourlyStatsRepository {
    suspend fun uploadData(data: List<HourlyStatsEntity>): Result<Unit>
    suspend fun getAllAppInfos(): List<HourlyStatsEntity>
    suspend fun insertAppInfos(data: List<HourlyStatsEntity>): List<Long>
    suspend fun deleteAppInfos(data: List<HourlyStatsEntity>): Int
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

    override suspend fun getAllAppInfos(): List<HourlyStatsEntity> {
        return dao.getAllHourlyStats()
    }

    override suspend fun insertAppInfos(data: List<HourlyStatsEntity>): List<Long> {
        return dao.insertHourlyStats(data)
    }

    override suspend fun deleteAppInfos(data: List<HourlyStatsEntity>): Int {
        return dao.deleteHourlyStats(data)
    }
}

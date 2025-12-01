package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.dao.UsageStatsDao
import com.dominik.control.kidshield.data.model.domain.UsageStatsEntity
import com.dominik.control.kidshield.data.remote.datasource.UsageStatsRemoteDataSource
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UsageStatsRepository {
    suspend fun uploadData(data: List<UsageStatsEntity>): Result<Unit>
    suspend fun getAllAppInfos(): List<UsageStatsEntity>
    suspend fun insertAppInfos(data: List<UsageStatsEntity>): List<Long>
    suspend fun deleteAppInfos(data: List<UsageStatsEntity>): Int
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

    override suspend fun getAllAppInfos(): List<UsageStatsEntity> {
        return dao.getAllUsageStats()
    }

    override suspend fun insertAppInfos(data: List<UsageStatsEntity>): List<Long> {
        return dao.insertUsageStats(data)
    }

    override suspend fun deleteAppInfos(data: List<UsageStatsEntity>): Int {
        return dao.deleteUsageStats(data)
    }
}

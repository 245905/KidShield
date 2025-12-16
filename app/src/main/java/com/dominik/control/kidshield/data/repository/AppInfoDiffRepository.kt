package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.dao.AppInfoDiffDao
import com.dominik.control.kidshield.data.model.domain.AppInfoDiffEntity
import com.dominik.control.kidshield.data.remote.datasource.AppInfoDiffRemoteDataSource
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AppInfoDiffRepository {
    suspend fun downloadData(): Result<List<AppInfoDiffEntity>>
    suspend fun getAllAppInfoDiffs(): List<AppInfoDiffEntity>
    suspend fun insertAppInfoDiffs(data: List<AppInfoDiffEntity>): List<Long>
    suspend fun deleteAppInfoDiffs(data: List<AppInfoDiffEntity>): Int
    suspend fun deleteAllAppInfoDiffs()
    suspend fun getAppInfoDiffByReferenceNumber(referenceNumber: Long): AppInfoDiffEntity
}

class AppInfoDiffRepositoryImpl @Inject constructor(
    private val remote: AppInfoDiffRemoteDataSource,
    private val dao: AppInfoDiffDao,              // Room
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppInfoDiffRepository {

    override suspend fun downloadData(): Result<List<AppInfoDiffEntity>> =
        withContext(ioDispatcher) {
            runCatching {
                remote.downloadData()
            }
        }

    override suspend fun getAllAppInfoDiffs(): List<AppInfoDiffEntity> {
        return dao.getAllAppInfoDiffs()
    }

    override suspend fun insertAppInfoDiffs(data: List<AppInfoDiffEntity>): List<Long> {
        return dao.insertAppInfoDiffs(data)
    }

    override suspend fun deleteAppInfoDiffs(data: List<AppInfoDiffEntity>): Int {
        return dao.deleteAppInfoDiffs(data)
    }

    override suspend fun deleteAllAppInfoDiffs() {
        return dao.deleteAll()
    }

    override suspend fun getAppInfoDiffByReferenceNumber(referenceNumber: Long): AppInfoDiffEntity {
        return dao.getAppInfoDiffByReferenceNumber(referenceNumber)
    }
}

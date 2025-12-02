package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.dao.AppInfoDao
import com.dominik.control.kidshield.data.model.domain.AppInfoEntity
import com.dominik.control.kidshield.data.remote.datasource.AppInfoRemoteDataSource
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AppInfoRepository {
    suspend fun uploadData(data: List<AppInfoEntity>): Result<Unit>
    suspend fun getAllAppInfos(): List<AppInfoEntity>
    suspend fun insertAppInfos(data: List<AppInfoEntity>): List<Long>
    suspend fun deleteAppInfos(data: List<AppInfoEntity>): Int
}

class AppInfoRepositoryImpl @Inject constructor(
    private val remote: AppInfoRemoteDataSource,
    private val dao: AppInfoDao,              // Room
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppInfoRepository {

    override suspend fun uploadData(data: List<AppInfoEntity>): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                remote.uploadData(data)
            }
        }

    override suspend fun getAllAppInfos(): List<AppInfoEntity> {
        return dao.getAllAppInfos()
    }

    override suspend fun insertAppInfos(data: List<AppInfoEntity>): List<Long> {
        return dao.insertAppInfos(data)
    }

    override suspend fun deleteAppInfos(data: List<AppInfoEntity>): Int {
        return dao.deleteAppInfos(data)
    }
}

package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.dao.AppInfoDao
import com.dominik.control.kidshield.data.model.domain.AppInfoEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import com.dominik.control.kidshield.data.remote.datasource.AppInfoRemoteDataSource
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AppInfoRepository {
    suspend fun uploadData(data: List<AppInfoEntity>): Result<Unit>
    suspend fun downloadData(): Result<List<AppInfoEntity>>
    suspend fun getAllAppInfos(): List<AppInfoEntity>
    suspend fun getUserAppInfos(): List<AppInfoEntity>
    suspend fun insertAppInfos(data: List<AppInfoEntity>): List<Long>
    suspend fun deleteAppInfos(data: List<AppInfoEntity>): Int
    suspend fun deleteAllAppInfos()
    suspend fun getPendingAppInfos(): List<AppInfoEntity>
    suspend fun updateUploadedAppInfos(data: List<AppInfoEntity>): Int
    suspend fun updateUploadingAppInfos(data: List<AppInfoEntity>): Int
    suspend fun updatePendingAppInfos(data: List<AppInfoEntity>): Int
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

    override suspend fun downloadData(): Result<List<AppInfoEntity>> =
        withContext(ioDispatcher) {
            runCatching {
                remote.downloadData()
            }
        }

    override suspend fun getAllAppInfos(): List<AppInfoEntity> {
        return dao.getAllAppInfos()
    }

    override suspend fun getUserAppInfos(): List<AppInfoEntity> {
        return dao.getUserAppInfos()
    }

    override suspend fun insertAppInfos(data: List<AppInfoEntity>): List<Long> {
        return dao.insertAppInfos(data)
    }

    override suspend fun deleteAppInfos(data: List<AppInfoEntity>): Int {
        return dao.deleteAppInfos(data)
    }

    override suspend fun deleteAllAppInfos() {
        return dao.deleteAll()
    }

    override suspend fun getPendingAppInfos(): List<AppInfoEntity> {
        return dao.getAppInfoByStatus(UploadStatusType.PENDING)
    }

    override suspend fun updateUploadedAppInfos(data: List<AppInfoEntity>): Int {
        val uploaded = data.map { it.copy(status = UploadStatusType.UPLOADED) }
        return dao.updateAppInfos(uploaded)
    }

    override suspend fun updateUploadingAppInfos(data: List<AppInfoEntity>): Int {
        val uploading = data.map { it.copy(status = UploadStatusType.UPLOADING) }
        return dao.updateAppInfos(uploading)
    }

    override suspend fun updatePendingAppInfos(data: List<AppInfoEntity>): Int {
        val pending = data.map { it.copy(status = UploadStatusType.PENDING) }
        return dao.updateAppInfos(pending)
    }
}

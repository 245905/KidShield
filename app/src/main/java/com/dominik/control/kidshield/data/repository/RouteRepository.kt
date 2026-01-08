package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.dao.PointDao
import com.dominik.control.kidshield.data.model.domain.PointEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import com.dominik.control.kidshield.data.remote.datasource.PointRemoteDataSource
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface RouteRepository {
    suspend fun insertPoint(point: PointEntity): Long
    suspend fun insertPoints(points: List<PointEntity>): List<Long>
    suspend fun uploadData(data: List<PointEntity>): Result<Unit>
    suspend fun downloadData(): Result<List<PointEntity>>
    suspend fun getAllPoints(): List<PointEntity>
    suspend fun deletePoints(data: List<PointEntity>): Int
    suspend fun deleteAllPoints()
    suspend fun getPointsNewerThan(timestamp: Long): List<PointEntity>
    suspend fun getPointsInInterval(startTime: Long, endTime: Long): List<PointEntity>
    suspend fun getPendingPoints(): List<PointEntity>
    suspend fun updateUploadedPoints(data: List<PointEntity>): Int
    suspend fun updateUploadingPoints(data: List<PointEntity>): Int
    suspend fun updatePendingPoints(data: List<PointEntity>): Int
}

class RouteRepositoryImpl @Inject constructor(
    private val remote: PointRemoteDataSource,
    private val dao: PointDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RouteRepository{

    override suspend fun insertPoint(point: PointEntity): Long {
        return dao.insertPoint(point)
    }

    override suspend fun insertPoints(points: List<PointEntity>): List<Long> {
        return dao.insertPoints(points)
    }

    override suspend fun uploadData(data: List<PointEntity>): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                remote.uploadData(data)
            }
        }

    override suspend fun downloadData(): Result<List<PointEntity>> =
        withContext(ioDispatcher) {
            runCatching {
                remote.downloadData()
            }
        }

    override suspend fun getAllPoints(): List<PointEntity> {
        return dao.getAllPoints()
    }

    override suspend fun deletePoints(data: List<PointEntity>): Int {
        return dao.deletePoints(data)
    }

    override suspend fun deleteAllPoints() {
        dao.deleteAll()
    }

    override suspend fun getPointsNewerThan(timestamp: Long): List<PointEntity>{
        return dao.getPointsNewerThan(timestamp)
    }

    override suspend fun getPointsInInterval(startTime: Long, endTime: Long): List<PointEntity>{
        return dao.getPointsInInterval(startTime, endTime)
    }

    override suspend fun getPendingPoints(): List<PointEntity> {
        return dao.getPointsByStatus(UploadStatusType.PENDING)
    }

    override suspend fun updateUploadedPoints(data: List<PointEntity>): Int {
        val uploaded = data.map { it.copy(status = UploadStatusType.UPLOADED) }
        return dao.updatePoints(uploaded)
    }

    override suspend fun updateUploadingPoints(data: List<PointEntity>): Int {
        val uploading = data.map { it.copy(status = UploadStatusType.UPLOADING) }
        return dao.updatePoints(uploading)
    }

    override suspend fun updatePendingPoints(data: List<PointEntity>): Int {
        val pending = data.map { it.copy(status = UploadStatusType.PENDING) }
        return dao.updatePoints(pending)
    }

}

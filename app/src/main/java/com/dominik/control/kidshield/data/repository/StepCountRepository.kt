package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.dao.StepCounterDao
import com.dominik.control.kidshield.data.model.domain.StepCountEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import com.dominik.control.kidshield.data.remote.datasource.StepCountRemoteDataSource
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface StepCountRepository {
    suspend fun insertStepCount(stepCount: StepCountEntity): Long
    suspend fun insertStepCounts(stepCounts: List<StepCountEntity>): List<Long>
    suspend fun uploadData(data: List<StepCountEntity>): Result<Unit>
    suspend fun downloadData(): Result<List<StepCountEntity>>
    suspend fun getAllStepCounts(): List<StepCountEntity>
    suspend fun deleteStepCounts(data: List<StepCountEntity>): Int
    suspend fun deleteAllStepCounts()
    suspend fun getStepCountsNewerThan(timestamp: Long): List<StepCountEntity>
    suspend fun getStepCountsInInterval(startTime: Long, endTime: Long): List<StepCountEntity>
    suspend fun getPendingStepCounts(): List<StepCountEntity>
    suspend fun updateUploadedStepCounts(data: List<StepCountEntity>): Int
    suspend fun updateUploadingStepCounts(data: List<StepCountEntity>): Int
    suspend fun updatePendingStepCounts(data: List<StepCountEntity>): Int
}

class StepCountRepositoryImpl @Inject constructor(
    private val remote: StepCountRemoteDataSource,
    private val dao: StepCounterDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : StepCountRepository{

    override suspend fun insertStepCount(stepCount: StepCountEntity): Long {
        return dao.insertStepCount(stepCount)
    }

    override suspend fun insertStepCounts(stepCounts: List<StepCountEntity>): List<Long> {
        return dao.insertStepCounts(stepCounts)
    }

    override suspend fun uploadData(data: List<StepCountEntity>): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                remote.uploadData(data)
            }
        }

    override suspend fun downloadData(): Result<List<StepCountEntity>> =
        withContext(ioDispatcher) {
            runCatching {
                remote.downloadData()
            }
        }

    override suspend fun getAllStepCounts(): List<StepCountEntity> {
        return dao.getAllStepCounts()
    }

    override suspend fun deleteStepCounts(data: List<StepCountEntity>): Int {
        return dao.deleteStepCounts(data)
    }

    override suspend fun deleteAllStepCounts() {
        dao.deleteAll()
    }

    override suspend fun getStepCountsNewerThan(timestamp: Long): List<StepCountEntity> {
        return dao.getStepCountsNewerThan(timestamp)
    }

    override suspend fun getStepCountsInInterval(startTime: Long, endTime: Long ): List<StepCountEntity> {
        return dao.getStepCountsInInterval(startTime, endTime)
    }

    override suspend fun getPendingStepCounts(): List<StepCountEntity> {
        return dao.getStepCountsByStatus(UploadStatusType.PENDING)
    }

    override suspend fun updateUploadedStepCounts(data: List<StepCountEntity>): Int {
        val uploaded = data.map { it.copy(status = UploadStatusType.UPLOADED) }
        return dao.updateStepCounts(uploaded)
    }

    override suspend fun updateUploadingStepCounts(data: List<StepCountEntity>): Int {
        val uploading = data.map { it.copy(status = UploadStatusType.UPLOADING) }
        return dao.updateStepCounts(uploading)
    }

    override suspend fun updatePendingStepCounts(data: List<StepCountEntity>): Int {
        val pending = data.map { it.copy(status = UploadStatusType.PENDING) }
        return dao.updateStepCounts(pending)
    }
}

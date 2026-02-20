package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.dao.SigMotionDao
import com.dominik.control.kidshield.data.model.domain.SigMotionEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import com.dominik.control.kidshield.data.remote.datasource.SigMotionRemoteDataSource
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface SigMotionRepository {
    suspend fun insertSigMotion(sigMotion: SigMotionEntity): Long
    suspend fun insertSigMotions(sigMotions: List<SigMotionEntity>): List<Long>
    suspend fun uploadData(data: List<SigMotionEntity>): Result<Unit>
    suspend fun downloadData(): Result<List<SigMotionEntity>>
    suspend fun getAllSigMotions(): List<SigMotionEntity>
    suspend fun deleteSigMotions(data: List<SigMotionEntity>): Int
    suspend fun deleteAllSigMotions()
    suspend fun getSigMotionsNewerThan(timestamp: Long): List<SigMotionEntity>
    suspend fun getSigMotionsInInterval(startTime: Long, endTime: Long): List<SigMotionEntity>
    suspend fun getPendingSigMotions(): List<SigMotionEntity>
    suspend fun updateUploadedSigMotions(data: List<SigMotionEntity>): Int
    suspend fun updateUploadingSigMotions(data: List<SigMotionEntity>): Int
    suspend fun updatePendingSigMotions(data: List<SigMotionEntity>): Int
}

class SigMotionRepositoryImpl @Inject constructor(
    private val remote: SigMotionRemoteDataSource,
    private val dao: SigMotionDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SigMotionRepository {

    override suspend fun insertSigMotion(sigMotion: SigMotionEntity): Long {
        return dao.insertSigMotion(sigMotion)
    }

    override suspend fun insertSigMotions(sigMotions: List<SigMotionEntity>): List<Long> {
        return dao.insertSigMotions(sigMotions)
    }

    override suspend fun uploadData(data: List<SigMotionEntity>): Result<Unit> =
        withContext(ioDispatcher) {
            runCatching {
                remote.uploadData(data)
            }
        }

    override suspend fun downloadData(): Result<List<SigMotionEntity>> =
        withContext(ioDispatcher) {
            runCatching {
                remote.downloadData()
            }
        }

    override suspend fun getAllSigMotions(): List<SigMotionEntity> {
        return dao.getAllSigMotions()
    }

    override suspend fun deleteSigMotions(data: List<SigMotionEntity>): Int {
        return dao.deleteSigMotions(data)
    }

    override suspend fun deleteAllSigMotions() {
        dao.deleteAll()
    }

    override suspend fun getSigMotionsNewerThan(timestamp: Long): List<SigMotionEntity> {
        return dao.getSigMotionsNewerThan(timestamp)
    }

    override suspend fun getSigMotionsInInterval( startTime: Long, endTime: Long ): List<SigMotionEntity> {
        return dao.getSigMotionsInInterval(startTime, endTime)
    }

    override suspend fun getPendingSigMotions(): List<SigMotionEntity> {
        return dao.getSigMotionsByStatus(UploadStatusType.PENDING)
    }

    override suspend fun updateUploadedSigMotions(data: List<SigMotionEntity>): Int {
        val uploaded = data.map { it.copy(status = UploadStatusType.UPLOADED) }
        return dao.updateSigMotions(uploaded)
    }

    override suspend fun updateUploadingSigMotions(data: List<SigMotionEntity>): Int {
        val uploading = data.map { it.copy(status = UploadStatusType.UPLOADING) }
        return dao.updateSigMotions(uploading)
    }

    override suspend fun updatePendingSigMotions(data: List<SigMotionEntity>): Int {
        val pending = data.map { it.copy(status = UploadStatusType.PENDING) }
        return dao.updateSigMotions(pending)
    }
}

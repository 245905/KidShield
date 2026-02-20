package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.model.domain.SigMotionEntity
import com.dominik.control.kidshield.data.model.domain.StepCountEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import jakarta.inject.Inject

interface SensorInfoRepository {
    suspend fun saveStepCount(steps: Long, ts: Long)
    suspend fun saveSignificantMotion(ts: Long)
}

class SensorInfoRepositoryImpl @Inject constructor(
    private val stepCount: StepCountRepository,
    private val sigMotion: SigMotionRepository
) : SensorInfoRepository{

    override suspend fun saveStepCount(steps: Long, ts: Long) {
        val step = StepCountEntity(
            steps = steps,
            timestamp = ts,
            status = UploadStatusType.PENDING
        )
        stepCount.insertStepCount(step)
    }

    override suspend fun saveSignificantMotion(ts: Long) {
        val step = SigMotionEntity(
            timestamp = ts,
            status = UploadStatusType.PENDING
        )
        sigMotion.insertSigMotion(step)
    }
}

package com.dominik.control.kidshield.data.model.dto

import com.dominik.control.kidshield.data.model.domain.SigMotionEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType

data class SigMotionDto(
    val timestamp: Long
)

fun SigMotionDto.toEntity(): SigMotionEntity = SigMotionEntity(
    timestamp = timestamp,
    status = UploadStatusType.UPLOADED,
)

fun SigMotionEntity.toDto(): SigMotionDto = SigMotionDto(
    timestamp = timestamp,
)

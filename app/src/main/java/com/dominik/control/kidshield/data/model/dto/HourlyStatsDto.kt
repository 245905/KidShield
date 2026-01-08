package com.dominik.control.kidshield.data.model.dto

import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import java.util.Date

data class HourlyStatsDto(
    val date: Long,
    val hour: Int,
    val totalTime: Long,
    val packageName: String?
)

fun HourlyStatsDto.toEntity(): HourlyStatsEntity = HourlyStatsEntity(
    date = Date(date),
    hour = hour,
    totalTime = totalTime,
    packageName = packageName,
    status = UploadStatusType.UPLOADED
)

fun HourlyStatsEntity.toDto(): HourlyStatsDto = HourlyStatsDto(
    date = date.time,
    hour = hour,
    totalTime = totalTime,
    packageName = packageName,
)

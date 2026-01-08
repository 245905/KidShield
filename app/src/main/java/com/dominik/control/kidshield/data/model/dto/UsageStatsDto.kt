package com.dominik.control.kidshield.data.model.dto

import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import com.dominik.control.kidshield.data.model.domain.UsageStatsEntity
import java.util.Date

data class UsageStatsDto(
    val date: Long,
    val packageName: String,

    val lastTimeUsed: Long,

    val totalTimeInForeground: Long, // main data
    val totalTimeVisible: Long
)

fun UsageStatsDto.toEntity(): UsageStatsEntity = UsageStatsEntity(
    date = Date(date),
    packageName = packageName,
    lastTimeUsed = lastTimeUsed,
    totalTimeInForeground = totalTimeInForeground,
    totalTimeVisible = totalTimeVisible,
    status = UploadStatusType.UPLOADED
)

fun UsageStatsEntity.toDto(): UsageStatsDto = UsageStatsDto(
    date = date.time,
    packageName = packageName,
    lastTimeUsed = lastTimeUsed,
    totalTimeInForeground = totalTimeInForeground,
    totalTimeVisible = totalTimeVisible
)

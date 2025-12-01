package com.dominik.control.kidshield.data.model.dto

import com.dominik.control.kidshield.data.model.domain.UsageStatsEntity
import java.util.Date

data class UsageStatsDto(
    val date: Date,
    val appName: String,
    val packageName: String,
    val isSystemApp: Boolean,

    val lastTimeUsed: Long,

    val totalTimeInForeground: Long, // main data
    val totalTimeVisible: Long
)

fun UsageStatsDto.toEntity(): UsageStatsEntity = UsageStatsEntity(
    date = date,
    appName = appName,
    packageName = packageName,
    isSystemApp = isSystemApp,
    lastTimeUsed = lastTimeUsed,
    totalTimeInForeground = totalTimeInForeground,
    totalTimeVisible = totalTimeVisible
)

fun UsageStatsEntity.toDto(): UsageStatsDto = UsageStatsDto(
    date = date,
    appName = appName,
    packageName = packageName,
    isSystemApp = isSystemApp,
    lastTimeUsed = lastTimeUsed,
    totalTimeInForeground = totalTimeInForeground,
    totalTimeVisible = totalTimeVisible
)

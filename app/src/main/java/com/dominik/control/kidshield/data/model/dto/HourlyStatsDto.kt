package com.dominik.control.kidshield.data.model.dto

import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
import java.util.Date

data class HourlyStatsDto(
    val date: Date,
    val hour: Int,
    val totalTime: Long,
    val packageName: String?
)

fun HourlyStatsDto.toEntity(): HourlyStatsEntity = HourlyStatsEntity(
    date = date,
    hour = hour,
    totalTime = totalTime,
    packageName = packageName,
)

fun HourlyStatsEntity.toDto(): HourlyStatsDto = HourlyStatsDto(
    date = date,
    hour = hour,
    totalTime = totalTime,
    packageName = packageName,
)

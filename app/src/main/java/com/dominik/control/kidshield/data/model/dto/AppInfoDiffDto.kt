package com.dominik.control.kidshield.data.model.dto

import com.dominik.control.kidshield.data.model.domain.AppChangeType
import com.dominik.control.kidshield.data.model.domain.AppInfoDiffEntity

data class AppInfoDiffDto(
    val referenceNumber:  Long,

    val changeType: AppChangeType,

    val appName: String,
    val packageName: String,
    val oldVersionName: String,
    val oldVersionCode: Long,
    val lastUpdateTime: Long
)

fun AppInfoDiffDto.toEntity(): AppInfoDiffEntity = AppInfoDiffEntity(
    appName = appName,
    packageName = packageName,
    lastUpdateTime = lastUpdateTime,
    referenceNumber = referenceNumber,
    changeType = changeType,
    oldVersionName = oldVersionName,
    oldVersionCode = oldVersionCode
)

fun AppInfoDiffEntity.toDto(): AppInfoDiffDto = AppInfoDiffDto(
    appName = appName,
    packageName = packageName,
    lastUpdateTime = lastUpdateTime,
    referenceNumber = referenceNumber,
    changeType = changeType,
    oldVersionName = oldVersionName,
    oldVersionCode = oldVersionCode
)

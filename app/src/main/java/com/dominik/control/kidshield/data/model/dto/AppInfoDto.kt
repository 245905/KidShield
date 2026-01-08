package com.dominik.control.kidshield.data.model.dto

import com.dominik.control.kidshield.data.model.domain.AppInfoEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType

data class AppInfoDto(
    val referenceNumber: Long?,
    val appName: String,
    val packageName: String,
    val versionName: String?,
    val versionCode: Long,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val isSystemApp: Boolean
)

fun AppInfoDto.toEntity(): AppInfoEntity = AppInfoEntity(
    appName = appName,
    packageName = packageName,
    versionName = versionName,
    versionCode = versionCode,
    firstInstallTime = firstInstallTime,
    lastUpdateTime = lastUpdateTime,
    isSystemApp = isSystemApp,
    referenceNumber = referenceNumber,
    status = UploadStatusType.UPLOADED
)

fun AppInfoEntity.toDto(): AppInfoDto = AppInfoDto(
    appName = appName,
    packageName = packageName,
    versionName = versionName,
    versionCode = versionCode,
    firstInstallTime = firstInstallTime,
    lastUpdateTime = lastUpdateTime,
    isSystemApp = isSystemApp,
    referenceNumber = referenceNumber
)

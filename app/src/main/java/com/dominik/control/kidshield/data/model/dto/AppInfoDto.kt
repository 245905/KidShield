package com.dominik.control.kidshield.data.model.dto

import com.dominik.control.kidshield.data.model.domain.AppInfoEntity

data class AppInfoDto(
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
    isSystemApp = isSystemApp
)

fun AppInfoEntity.toDto(): AppInfoDto = AppInfoDto(
    appName = appName,
    packageName = packageName,
    versionName = versionName,
    versionCode = versionCode,
    firstInstallTime = firstInstallTime,
    lastUpdateTime = lastUpdateTime,
    isSystemApp = isSystemApp
)

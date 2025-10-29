package com.dominik.control.kidshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_infos")
data class AppInfoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val packageName: String,
    val versionName: String?,
    val versionCode: Long,
    val firstInstallTime: Long,
    val lastUpdateTime: Long,
    val isSystemApp: Boolean
)

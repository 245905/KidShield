package com.dominik.control.kidshield.data.model.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_info_diffs")
data class AppInfoDiffEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    val referenceNumber:  Long,

    val changeType: AppChangeType,

    val appName: String,
    val packageName: String,
    val oldVersionName: String,
    val oldVersionCode: Long,
    val lastUpdateTime: Long
)

enum class AppChangeType {
    ADDED,
    REMOVED,
    UPDATED
}

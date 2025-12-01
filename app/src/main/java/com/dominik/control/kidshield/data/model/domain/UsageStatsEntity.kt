package com.dominik.control.kidshield.data.model.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "usage_stats")
data class UsageStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Date,
    val appName: String,
    val packageName: String,
    val isSystemApp: Boolean,

    val lastTimeUsed: Long,

    val totalTimeInForeground: Long, // main data
    val totalTimeVisible: Long
)

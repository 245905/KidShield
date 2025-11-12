package com.dominik.control.kidshield.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "hourly_stats")
data class HourlyStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Date,
    val hour: Int,
    val totalTime: Long,
    val packageName: String?
)

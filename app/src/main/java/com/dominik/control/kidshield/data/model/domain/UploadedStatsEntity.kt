package com.dominik.control.kidshield.data.model.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "uploaded_stats")
data class UploadedStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Date,
    val tableType: UploadTableType,
)

enum class UploadTableType {
    HOURLY,
    USAGE
}

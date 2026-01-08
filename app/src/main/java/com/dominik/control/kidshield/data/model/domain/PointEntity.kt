package com.dominik.control.kidshield.data.model.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points")
data class PointEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lat: Double,
    val lon: Double,
    val speed: Float,
    val fixTime: Long,       // timestamp
    val receivedTime: Long,  // time for processing

    val status: UploadStatusType
)

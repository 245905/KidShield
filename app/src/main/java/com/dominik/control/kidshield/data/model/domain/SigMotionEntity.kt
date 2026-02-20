package com.dominik.control.kidshield.data.model.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sigmotion")
data class SigMotionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,

    val status: UploadStatusType
)

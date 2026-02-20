package com.dominik.control.kidshield.data.model.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stepcounter")
data class StepCountEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val steps: Long,
    val timestamp: Long,

    val status: UploadStatusType
)

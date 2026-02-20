package com.dominik.control.kidshield.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dominik.control.kidshield.data.model.domain.StepCountEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType

@Dao
interface StepCounterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepCount(stepCount: StepCountEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepCounts(stepCounts: List<StepCountEntity>): List<Long>

    @Delete
    suspend fun deleteStepCount(stepCount: StepCountEntity): Int

    @Delete
    suspend fun deleteStepCounts(stepCounts: List<StepCountEntity>): Int

    @Query("DELETE FROM stepcounter")
    suspend fun deleteAll()

    @Query("SELECT * FROM stepcounter")
    suspend fun getAllStepCounts(): List<StepCountEntity>

    @Query("SELECT * FROM stepcounter WHERE timestamp > :timestamp ORDER BY timestamp ASC")
    suspend fun getStepCountsNewerThan(timestamp: Long): List<StepCountEntity>

    @Query("SELECT * FROM stepcounter WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    suspend fun getStepCountsInInterval(startTime: Long, endTime: Long): List<StepCountEntity>

    @Query("SELECT * FROM stepcounter WHERE status = :status")
    suspend fun getStepCountsByStatus(status: UploadStatusType): List<StepCountEntity>

    @Update
    suspend fun updateStepCounts(stepCounts: List<StepCountEntity>): Int
}

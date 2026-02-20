package com.dominik.control.kidshield.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dominik.control.kidshield.data.model.domain.SigMotionEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType

@Dao
interface SigMotionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSigMotion(sigMotion: SigMotionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSigMotions(sigMotions: List<SigMotionEntity>): List<Long>

    @Delete
    suspend fun deleteSigMotion(sigMotion: SigMotionEntity): Int

    @Delete
    suspend fun deleteSigMotions(sigMotions: List<SigMotionEntity>): Int

    @Query("DELETE FROM sigmotion")
    suspend fun deleteAll()

    @Query("SELECT * FROM sigmotion")
    suspend fun getAllSigMotions(): List<SigMotionEntity>

    @Query("SELECT * FROM sigmotion WHERE timestamp > :timestamp ORDER BY timestamp ASC")
    suspend fun getSigMotionsNewerThan(timestamp: Long): List<SigMotionEntity>

    @Query("SELECT * FROM sigmotion WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    suspend fun getSigMotionsInInterval(startTime: Long, endTime: Long): List<SigMotionEntity>

    @Query("SELECT * FROM sigmotion WHERE status = :status")
    suspend fun getSigMotionsByStatus(status: UploadStatusType): List<SigMotionEntity>

    @Update
    suspend fun updateSigMotions(sigMotions: List<SigMotionEntity>): Int
}

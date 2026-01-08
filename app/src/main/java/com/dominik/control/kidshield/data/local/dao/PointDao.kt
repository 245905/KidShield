package com.dominik.control.kidshield.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dominik.control.kidshield.data.model.domain.PointEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType

@Dao
interface PointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: PointEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<PointEntity>): List<Long>

    @Delete
    suspend fun deletePoint(point: PointEntity): Int

    @Delete
    suspend fun deletePoints(points: List<PointEntity>): Int

    @Query("DELETE FROM points")
    suspend fun deleteAll()

    @Query("SELECT * FROM points")
    suspend fun getAllPoints(): List<PointEntity>

    @Query("SELECT * FROM points WHERE receivedTime > :timestamp ORDER BY receivedTime ASC")
    suspend fun getPointsNewerThan(timestamp: Long): List<PointEntity>

    @Query("SELECT * FROM points WHERE receivedTime BETWEEN :startTime AND :endTime ORDER BY receivedTime ASC")
    suspend fun getPointsInInterval(startTime: Long, endTime: Long): List<PointEntity>

    @Query("SELECT * FROM points WHERE status = :status")
    suspend fun getPointsByStatus(status: UploadStatusType): List<PointEntity>

    @Update
    suspend fun updatePoints(points: List<PointEntity>): Int
}

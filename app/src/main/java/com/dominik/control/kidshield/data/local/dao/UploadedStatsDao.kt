package com.dominik.control.kidshield.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dominik.control.kidshield.data.model.domain.UploadTableType
import com.dominik.control.kidshield.data.model.domain.UploadedStatsEntity
import java.util.Date

@Dao
interface UploadedStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUploadedStats(uploadedStats: UploadedStatsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUploadedStats(uploadedStats: List<UploadedStatsEntity>): List<Long>

    @Delete
    suspend fun deleteUploadedStats(uploadedStats: UploadedStatsEntity): Int

    @Delete
    suspend fun deleteUploadedStats(uploadedStats: List<UploadedStatsEntity>): Int

    @Query("DELETE FROM uploaded_stats WHERE tableType = :tableType")
    suspend fun deleteByTableType(tableType: UploadTableType): Int

    @Query("DELETE FROM uploaded_stats")
    suspend fun deleteAll()

    @Query("SELECT * FROM uploaded_stats")
    suspend fun getAllUploadedStats(): List<UploadedStatsEntity>

    @Query("SELECT * FROM uploaded_stats WHERE tableType = :tableType")
    suspend fun getUploadedStatsByTableType(tableType: UploadTableType): List<UploadedStatsEntity>

    @Query("SELECT * FROM uploaded_stats WHERE date = :date")
    suspend fun getUploadedStatsByDate(date: Date): List<UploadedStatsEntity>

    @Query("SELECT * FROM uploaded_stats WHERE date = :date AND tableType = :tableType")
    suspend fun getUploadedStatsByDateAndTableType(date: Date, tableType: UploadTableType): UploadedStatsEntity?

    @Update
    suspend fun updateUploadedStats(uploadedStats: List<UploadedStatsEntity>): Int
}

package com.dominik.control.kidshield.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import java.util.Date

@Dao
interface HourlyStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyStats(hourlyStats: HourlyStatsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyStats(hourlyStats: List<HourlyStatsEntity>): List<Long>

    @Delete
    suspend fun deleteHourlyStats(hourlyStats: HourlyStatsEntity): Int

    @Delete
    suspend fun deleteHourlyStats(hourlyStats: List<HourlyStatsEntity>): Int

    @Query("DELETE FROM hourly_stats WHERE packageName IN (:packages)")
    suspend fun deleteByPackages(packages: List<String>): Int

    @Query("DELETE FROM hourly_stats")
    suspend fun deleteAll()

    @Query("DELETE FROM hourly_stats WHERE date = :date")
    suspend fun deleteByDate(date: Date): Int

    @Query("SELECT * FROM hourly_stats")
    suspend fun getAllHourlyStats(): List<HourlyStatsEntity>

    @Query("SELECT h.id,h.date,h.hour,h.totalTime,h.packageName,h.status FROM hourly_stats h JOIN app_infos a ON h.packageName=a.packageName WHERE isSystemApp = TRUE")
    suspend fun getSystemAppHourlyStats(): List<HourlyStatsEntity>

    @Query("SELECT h.id,h.date,h.hour,h.totalTime,h.packageName,h.status FROM hourly_stats h JOIN app_infos a ON h.packageName=a.packageName WHERE appName = :appName")
    suspend fun getHourlyStatsByAppName(appName: String): HourlyStatsEntity

    @Query("SELECT * FROM hourly_stats WHERE packageName = :packageName")
    suspend fun getHourlyStatsByPackageName(packageName: String): HourlyStatsEntity

    @Query("SELECT * FROM hourly_stats WHERE date = :date")
    suspend fun getHourlyStatsByDate(date: Date): HourlyStatsEntity

    @Query("SELECT * FROM hourly_stats WHERE status = :status")
    suspend fun getHourlyStatsByStatus(status: UploadStatusType): List<HourlyStatsEntity>

    @Update
    suspend fun updateHourlyStats(hourlyStats: List<HourlyStatsEntity>): Int

}

package com.dominik.control.kidshield.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dominik.control.kidshield.data.model.HourlyStatsEntity
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

    @Query("SELECT * FROM hourly_stats")
    suspend fun getAllHourlyStats(): List<HourlyStatsEntity>

    @Query("SELECT h.id,h.date,h.hour,h.totalTime,h.packageName FROM hourly_stats h JOIN app_infos a ON h.packageName=a.packageName WHERE isSystemApp = TRUE")
    suspend fun getSystemAppHourlyStats(): List<HourlyStatsEntity>

    @Query("SELECT h.id,h.date,h.hour,h.totalTime,h.packageName FROM hourly_stats h JOIN app_infos a ON h.packageName=a.packageName WHERE appName = :appName")
    suspend fun getHourlyStatsByAppName(appName: String): HourlyStatsEntity

    @Query("SELECT * FROM hourly_stats WHERE packageName = :packageName")
    suspend fun getHourlyStatsByPackageName(packageName: String): HourlyStatsEntity

    @Query("SELECT * FROM hourly_stats WHERE date = :date")
    suspend fun getHourlyStatsByDate(date: Date): HourlyStatsEntity

}

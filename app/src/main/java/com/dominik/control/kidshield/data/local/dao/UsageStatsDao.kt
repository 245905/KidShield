package com.dominik.control.kidshield.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
import com.dominik.control.kidshield.data.model.domain.UploadStatusType
import com.dominik.control.kidshield.data.model.domain.UsageStatsEntity
import java.util.Date

@Dao
interface UsageStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageStats(usageStats: UsageStatsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageStats(usageStats: List<UsageStatsEntity>): List<Long>

    @Delete
    suspend fun deleteUsageStats(usageStats: UsageStatsEntity): Int

    @Delete
    suspend fun deleteUsageStats(usageStats: List<UsageStatsEntity>): Int

    @Query("DELETE FROM usage_stats WHERE packageName IN (:packages)")
    suspend fun deleteByPackages(packages: List<String>): Int

    @Query("DELETE FROM usage_stats WHERE date = :date")
    suspend fun deleteByDate(date: Date): Int

    @Query("DELETE FROM usage_stats")
    suspend fun deleteAll()

    @Query("SELECT * FROM usage_stats")
    suspend fun getAllUsageStats(): List<UsageStatsEntity>

    @Query("SELECT u.id,u.date,u.lastTimeUsed,u.totalTimeInForeground,u.totalTimeVisible,u.packageName,u.status FROM usage_stats u JOIN app_infos a ON u.packageName=a.packageName WHERE isSystemApp = TRUE")
    suspend fun getSystemAppUsageStats(): List<UsageStatsEntity>

    @Query("SELECT u.id,u.date,u.lastTimeUsed,u.totalTimeInForeground,u.totalTimeVisible,u.packageName,u.status FROM usage_stats u JOIN app_infos a ON u.packageName=a.packageName WHERE appName = :appName")
    suspend fun getUsageStatsByAppName(appName: String): UsageStatsEntity

    @Query("SELECT * FROM usage_stats WHERE packageName = :packageName")
    suspend fun getUsageStatsByPackageName(packageName: String): UsageStatsEntity

    @Query("SELECT * FROM usage_stats WHERE date = :date")
    suspend fun getUsageStatsByDate(date: Date): UsageStatsEntity

    @Query("SELECT * FROM usage_stats WHERE status = :status")
    suspend fun getUsageStatsByStatus(status: UploadStatusType): List<UsageStatsEntity>

    @Update
    suspend fun updateUsageStats(usageStats: List<UsageStatsEntity>): Int

}

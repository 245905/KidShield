package com.dominik.control.kidshield.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dominik.control.kidshield.data.model.UsageStatsEntity
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

    @Query("DELETE FROM usage_stats")
    suspend fun deleteAll()

    @Query("SELECT * FROM usage_stats")
    suspend fun getAllUsageStats(): List<UsageStatsEntity>

    @Query("SELECT * FROM usage_stats WHERE isSystemApp = TRUE")
    suspend fun getSystemAppUsageStats(): List<UsageStatsEntity>

    @Query("SELECT * FROM usage_stats WHERE appName = :appName")
    suspend fun getUsageStatsByAppName(appName: String): UsageStatsEntity

    @Query("SELECT * FROM usage_stats WHERE packageName = :packageName")
    suspend fun getUsageStatsByPackageName(packageName: String): UsageStatsEntity

    @Query("SELECT * FROM usage_stats WHERE date = :date")
    suspend fun getUsageStatsByDate(date: Date): UsageStatsEntity

}

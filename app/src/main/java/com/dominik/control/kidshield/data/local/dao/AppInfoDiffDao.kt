package com.dominik.control.kidshield.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dominik.control.kidshield.data.model.domain.AppInfoDiffEntity

@Dao
interface AppInfoDiffDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppInfoDiff(appInfoDiff: AppInfoDiffEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppInfoDiffs(appInfoDiffs: List<AppInfoDiffEntity>): List<Long>

    @Delete
    suspend fun deleteAppInfoDiff(appInfoDiff: AppInfoDiffEntity): Int

    @Delete
    suspend fun deleteAppInfoDiffs(appInfoDiffs: List<AppInfoDiffEntity>): Int

    @Query("DELETE FROM app_info_diffs WHERE packageName IN (:packages)")
    suspend fun deleteByPackages(packages: List<String>): Int

    @Query("DELETE FROM app_info_diffs")
    suspend fun deleteAll()

    @Query("SELECT * FROM app_info_diffs")
    suspend fun getAllAppInfoDiffs(): List<AppInfoDiffEntity>

    @Query("SELECT * FROM app_info_diffs WHERE appName = :appName")
    suspend fun getAppInfoDiffByAppName(appName: String): AppInfoDiffEntity

    @Query("SELECT * FROM app_info_diffs WHERE packageName = :packageName")
    suspend fun getAppInfoDiffByPackageName(packageName: String): AppInfoDiffEntity

    @Query("SELECT * FROM app_info_diffs WHERE referenceNumber = :referenceNumber")
    suspend fun getAppInfoDiffByReferenceNumber(referenceNumber: Long): AppInfoDiffEntity
}

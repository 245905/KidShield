package com.dominik.control.kidshield.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dominik.control.kidshield.data.model.AppInfoEntity

@Dao
interface AppInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppInfo(appInfo: AppInfoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppInfos(appInfos: List<AppInfoEntity>): List<Long>

    @Delete
    suspend fun deleteAppInfo(appInfo: AppInfoEntity): Int

    @Delete
    suspend fun deleteAppInfos(appInfos: List<AppInfoEntity>): Int

    @Query("DELETE FROM app_infos WHERE packageName IN (:packages)")
    suspend fun deleteByPackages(packages: List<String>): Int

    @Query("DELETE FROM app_infos")
    suspend fun deleteAll()

    @Query("SELECT * FROM app_infos")
    suspend fun getAllAppInfos(): List<AppInfoEntity>

    @Query("SELECT * FROM app_infos WHERE isSystemApp = TRUE")
    suspend fun getSystemAppInfos(): List<AppInfoEntity>

    @Query("SELECT * FROM app_infos WHERE appName = :appName")
    suspend fun getAppInfoByAppName(appName: String): AppInfoEntity

    @Query("SELECT * FROM app_infos WHERE packageName = :packageName")
    suspend fun getAppInfoByPackageName(packageName: String): AppInfoEntity

}

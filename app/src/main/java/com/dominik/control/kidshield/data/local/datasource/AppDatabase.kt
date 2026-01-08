package com.dominik.control.kidshield.data.local.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dominik.control.kidshield.data.local.dao.AppInfoDao
import com.dominik.control.kidshield.data.local.dao.AppInfoDiffDao
import com.dominik.control.kidshield.data.local.dao.HourlyStatsDao
import com.dominik.control.kidshield.data.local.dao.PointDao
import com.dominik.control.kidshield.data.local.dao.UploadedStatsDao
import com.dominik.control.kidshield.data.local.dao.UsageStatsDao
import com.dominik.control.kidshield.data.model.domain.AppInfoDiffEntity
import com.dominik.control.kidshield.data.model.domain.AppInfoEntity
import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
import com.dominik.control.kidshield.data.model.domain.PointEntity
import com.dominik.control.kidshield.data.model.domain.UploadedStatsEntity
import com.dominik.control.kidshield.data.model.domain.UsageStatsEntity
import com.dominik.control.kidshield.utils.Converters

@Database(entities = [AppInfoEntity::class, UsageStatsEntity::class, HourlyStatsEntity::class, AppInfoDiffEntity::class, UploadedStatsEntity::class, PointEntity::class], version = 5)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun appInfoDao(): AppInfoDao
    abstract fun usageStatsDao(): UsageStatsDao
    abstract fun hourlyStatsDao(): HourlyStatsDao
    abstract fun appInfoDiffDao(): AppInfoDiffDao
    abstract fun uploadedStatsDao(): UploadedStatsDao
    abstract fun pointDao(): PointDao

}
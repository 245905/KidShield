package com.dominik.control.kidshield.data.local.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dominik.control.kidshield.data.local.dao.AppInfoDao
import com.dominik.control.kidshield.data.local.dao.HourlyStatsDao
import com.dominik.control.kidshield.data.local.dao.UsageStatsDao
import com.dominik.control.kidshield.data.model.domain.AppInfoEntity
import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
import com.dominik.control.kidshield.data.model.domain.UsageStatsEntity
import com.dominik.control.kidshield.utils.Converters

@Database(entities = [AppInfoEntity::class, UsageStatsEntity::class, HourlyStatsEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun appInfoDao(): AppInfoDao
    abstract fun usageStatsDao(): UsageStatsDao
    abstract fun hourlyStatsDao(): HourlyStatsDao

}
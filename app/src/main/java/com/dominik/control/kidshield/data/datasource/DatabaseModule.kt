package com.dominik.control.kidshield.data.datasource

import android.content.Context
import androidx.room.Room
import com.dominik.control.kidshield.data.dao.AppInfoDao
import com.dominik.control.kidshield.data.dao.HourlyStatsDao
import com.dominik.control.kidshield.data.dao.UsageStatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "kidshield_database"
        ).build()
    }

    @Provides
    fun provideAppInfoDao(db: AppDatabase): AppInfoDao {
        return db.appInfoDao()
    }

    @Provides
    fun provideUsageStatsDao(db: AppDatabase): UsageStatsDao {
        return db.usageStatsDao()
    }

    @Provides
    fun provideHourlyStatsDao(db: AppDatabase): HourlyStatsDao {
        return db.hourlyStatsDao()
    }
}

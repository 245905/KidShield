package com.dominik.control.kidshield.di

import android.content.Context
import androidx.room.Room
import com.dominik.control.kidshield.data.local.dao.AppInfoDao
import com.dominik.control.kidshield.data.local.dao.AppInfoDiffDao
import com.dominik.control.kidshield.data.local.dao.HourlyStatsDao
import com.dominik.control.kidshield.data.local.dao.PointDao
import com.dominik.control.kidshield.data.local.dao.UploadedStatsDao
import com.dominik.control.kidshield.data.local.dao.UsageStatsDao
import com.dominik.control.kidshield.data.local.datasource.AppDatabase
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
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAppInfoDao(db: AppDatabase): AppInfoDao {
        return db.appInfoDao()
    }

    @Provides
    fun provideAppInfoDiffDao(db: AppDatabase): AppInfoDiffDao {
        return db.appInfoDiffDao()
    }

    @Provides
    fun provideUsageStatsDao(db: AppDatabase): UsageStatsDao {
        return db.usageStatsDao()
    }

    @Provides
    fun provideHourlyStatsDao(db: AppDatabase): HourlyStatsDao {
        return db.hourlyStatsDao()
    }

    @Provides
    fun provideUploadedStatsDao(db: AppDatabase): UploadedStatsDao {
        return db.uploadedStatsDao()
    }

    @Provides
    fun providePointDao(db: AppDatabase): PointDao {
        return db.pointDao()
    }
}

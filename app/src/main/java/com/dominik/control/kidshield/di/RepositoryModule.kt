package com.dominik.control.kidshield.di

import com.dominik.control.kidshield.data.repository.AppInfoRepository
import com.dominik.control.kidshield.data.repository.AppInfoRepositoryImpl
import com.dominik.control.kidshield.data.repository.HourlyStatsRepository
import com.dominik.control.kidshield.data.repository.HourlyStatsRepositoryImpl
import com.dominik.control.kidshield.data.repository.TestRepository
import com.dominik.control.kidshield.data.repository.TestRepositoryImpl
import com.dominik.control.kidshield.data.repository.UsageStatsRepository
import com.dominik.control.kidshield.data.repository.UsageStatsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule{
    @Binds
    abstract fun bindAppInfoRepo(impl: AppInfoRepositoryImpl): AppInfoRepository

    @Binds
    abstract fun bindUsageStatsRepo(impl: UsageStatsRepositoryImpl): UsageStatsRepository

    @Binds
    abstract fun bindTestRepo(impl: TestRepositoryImpl): TestRepository

    @Binds
    abstract fun bindHourlyStatsRepo(impl: HourlyStatsRepositoryImpl): HourlyStatsRepository

}

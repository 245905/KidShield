package com.dominik.control.kidshield.di

import com.dominik.control.kidshield.data.repository.AppInfoDiffRepository
import com.dominik.control.kidshield.data.repository.AppInfoDiffRepositoryImpl
import com.dominik.control.kidshield.data.repository.AppInfoRepository
import com.dominik.control.kidshield.data.repository.AppInfoRepositoryImpl
import com.dominik.control.kidshield.data.repository.AuthRepository
import com.dominik.control.kidshield.data.repository.AuthRepositoryImpl
import com.dominik.control.kidshield.data.repository.HourlyStatsRepository
import com.dominik.control.kidshield.data.repository.HourlyStatsRepositoryImpl
import com.dominik.control.kidshield.data.repository.RouteRepository
import com.dominik.control.kidshield.data.repository.RouteRepositoryImpl
import com.dominik.control.kidshield.data.repository.TestRepository
import com.dominik.control.kidshield.data.repository.TestRepositoryImpl
import com.dominik.control.kidshield.data.repository.UploadedStatsRepository
import com.dominik.control.kidshield.data.repository.UploadedStatsRepositoryImpl
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
    abstract fun bindAppInfoDiffRepo(impl: AppInfoDiffRepositoryImpl): AppInfoDiffRepository

    @Binds
    abstract fun bindUsageStatsRepo(impl: UsageStatsRepositoryImpl): UsageStatsRepository

    @Binds
    abstract fun bindTestRepo(impl: TestRepositoryImpl): TestRepository

    @Binds
    abstract fun bindHourlyStatsRepo(impl: HourlyStatsRepositoryImpl): HourlyStatsRepository

    @Binds
    abstract fun bindAuthRepo(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindRouteRepo(impl: RouteRepositoryImpl): RouteRepository

    @Binds
    abstract fun bindUploadedStatsRepo(impl: UploadedStatsRepositoryImpl): UploadedStatsRepository
}

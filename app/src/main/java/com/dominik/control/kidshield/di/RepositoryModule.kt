package com.dominik.control.kidshield.di

import com.dominik.control.kidshield.data.repository.AppInfoRepository
import com.dominik.control.kidshield.data.repository.AppInfoRepositoryImpl
import com.dominik.control.kidshield.data.repository.TestRepository
import com.dominik.control.kidshield.data.repository.TestRepositoryImpl
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
    abstract fun bindTestRepo(impl: TestRepositoryImpl): TestRepository

}

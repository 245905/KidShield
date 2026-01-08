package com.dominik.control.kidshield.di

import android.content.Context
import com.dominik.control.kidshield.data.core.providers.AppInfoProvider
import com.dominik.control.kidshield.data.core.providers.AppTimeProvider
import com.dominik.control.kidshield.data.core.providers.LocationProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {

    @Provides
    fun provideAppInfoProvider(@ApplicationContext appContext: Context): AppInfoProvider {
        return AppInfoProvider(appContext)
    }

    @Provides
    fun provideAppTimeProvider(@ApplicationContext appContext: Context): AppTimeProvider {
        return AppTimeProvider(appContext)
    }

    @Provides
    fun provideLocationProvider(@ApplicationContext appContext: Context): LocationProvider {
        return LocationProvider(appContext)
    }
}

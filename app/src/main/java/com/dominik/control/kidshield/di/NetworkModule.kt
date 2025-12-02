package com.dominik.control.kidshield.di

import android.content.Context
import com.dominik.control.kidshield.data.remote.api.AppInfoApi
import com.dominik.control.kidshield.data.remote.api.AuthApi
import com.dominik.control.kidshield.data.remote.api.HourlyStatsApi
import com.dominik.control.kidshield.data.remote.api.PairingApi
import com.dominik.control.kidshield.data.remote.api.TestApi
import com.dominik.control.kidshield.data.remote.api.UsageStatsApi
import com.dominik.control.kidshield.data.remote.retrofit.AuthInterceptor
import com.dominik.control.kidshield.data.remote.retrofit.TokenAuthenticator
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Qualifier
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext ctx: Context,
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator) // handles 401 -> refresh
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    }

    @Provides @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        @ApplicationContext context: Context
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.33.111:8082/")
            .addConverterFactory(GsonConverterFactory.create())
            .build() // UWAGA: bez interceptora z tokenem!
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@AuthRetrofit retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    @MainRetrofit
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://192.168.33.111:8082/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun providePairingApi(@MainRetrofit retrofit: Retrofit): PairingApi =
        retrofit.create(PairingApi::class.java)

    @Provides
    @Singleton
    fun provideAppInfoApi(@MainRetrofit retrofit: Retrofit): AppInfoApi =
        retrofit.create(AppInfoApi::class.java)

    @Provides
    @Singleton
    fun provideUsageStatsApi(@MainRetrofit retrofit: Retrofit): UsageStatsApi =
        retrofit.create(UsageStatsApi::class.java)

    @Provides
    @Singleton
    fun provideHourlyStatsApi(@MainRetrofit retrofit: Retrofit): HourlyStatsApi =
        retrofit.create(HourlyStatsApi::class.java)

    @Provides
    @Singleton
    fun provideTestApi(@MainRetrofit retrofit: Retrofit): TestApi =
        retrofit.create(TestApi::class.java)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainRetrofit

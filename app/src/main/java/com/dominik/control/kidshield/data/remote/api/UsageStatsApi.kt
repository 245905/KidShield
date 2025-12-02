package com.dominik.control.kidshield.data.remote.api

import com.dominik.control.kidshield.data.model.dto.UsageStatsDto
import retrofit2.http.Body
import retrofit2.http.POST

interface UsageStatsApi {

    @POST("api/v1/usageStats")
    suspend fun uploadData(@Body data: List<UsageStatsDto>): String
}
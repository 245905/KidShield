package com.dominik.control.kidshield.data.remote.api

import com.dominik.control.kidshield.data.model.dto.HourlyStatsDto
import retrofit2.http.Body
import retrofit2.http.POST

interface HourlyStatsApi {

    @POST("api/v1/hourlyStats")
    suspend fun uploadData(@Body data: List<HourlyStatsDto>): String

}

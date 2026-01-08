package com.dominik.control.kidshield.data.remote.api

import com.dominik.control.kidshield.data.model.dto.MessageResponse
import com.dominik.control.kidshield.data.model.dto.PointDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PointApi {
    @POST("api/v1/point")
    suspend fun uploadData(@Body data: List<PointDto>): MessageResponse;

    @GET("api/v1/point")
    suspend fun downloadData(): List<PointDto>;
}

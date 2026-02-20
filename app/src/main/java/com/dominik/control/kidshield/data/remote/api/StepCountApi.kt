package com.dominik.control.kidshield.data.remote.api

import com.dominik.control.kidshield.data.model.dto.MessageResponse
import com.dominik.control.kidshield.data.model.dto.StepCountDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StepCountApi {
    @POST("api/v1/stepCount")
    suspend fun uploadData(@Body data: List<StepCountDto>): MessageResponse;

    @GET("api/v1/stepCount")
    suspend fun downloadData(): List<StepCountDto>;
}

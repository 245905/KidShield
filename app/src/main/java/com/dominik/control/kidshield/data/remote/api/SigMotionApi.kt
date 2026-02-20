package com.dominik.control.kidshield.data.remote.api

import com.dominik.control.kidshield.data.model.dto.MessageResponse
import com.dominik.control.kidshield.data.model.dto.SigMotionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SigMotionApi {
    @POST("api/v1/sigMotion")
    suspend fun uploadData(@Body data: List<SigMotionDto>): MessageResponse;

    @GET("api/v1/sigMotion")
    suspend fun downloadData(): List<SigMotionDto>;
}

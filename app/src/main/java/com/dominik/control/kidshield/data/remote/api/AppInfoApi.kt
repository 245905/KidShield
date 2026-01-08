package com.dominik.control.kidshield.data.remote.api

import com.dominik.control.kidshield.data.model.dto.AppInfoDto
import com.dominik.control.kidshield.data.model.dto.MessageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AppInfoApi {

    @POST("api/v1/appInfo")
    suspend fun uploadData(@Body data: List<AppInfoDto>): MessageResponse;

    @GET("api/v1/appInfo")
    suspend fun downloadData(): List<AppInfoDto>;

}

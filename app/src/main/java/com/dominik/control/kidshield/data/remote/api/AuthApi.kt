package com.dominik.control.kidshield.data.remote.api

import com.dominik.control.kidshield.data.model.dto.RefreshRequest
import com.dominik.control.kidshield.data.model.dto.RefreshResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): RefreshResponse

}

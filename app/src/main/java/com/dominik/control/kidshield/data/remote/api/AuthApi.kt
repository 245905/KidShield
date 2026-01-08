package com.dominik.control.kidshield.data.remote.api

import com.dominik.control.kidshield.data.model.dto.RefreshRequest
import com.dominik.control.kidshield.data.model.dto.AuthResponse
import com.dominik.control.kidshield.data.model.dto.LoginRequest
import com.dominik.control.kidshield.data.model.dto.MessageResponse
import com.dominik.control.kidshield.data.model.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): AuthResponse

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest)

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body request: RefreshRequest): MessageResponse

    @POST("api/v1/auth/logout-all")
    suspend fun logoutAll(@Body request: RefreshRequest): MessageResponse
}

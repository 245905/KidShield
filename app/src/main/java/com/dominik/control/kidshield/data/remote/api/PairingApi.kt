package com.dominik.control.kidshield.data.remote.api

import com.dominik.control.kidshield.data.model.dto.GenerateCodeResponse
import com.dominik.control.kidshield.data.model.dto.PairByPinRequest
import com.dominik.control.kidshield.data.model.dto.PairByUUIDRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface PairingApi {

    @POST("api/v1/pair/code")
    suspend fun generateCode(): GenerateCodeResponse

    @POST("api/v1/pair/qr")
    suspend fun pairByUUID(@Body request: PairByUUIDRequest)

    @POST("api/v1/pair/pin")
    suspend fun pairByPin(@Body request: PairByPinRequest)
}

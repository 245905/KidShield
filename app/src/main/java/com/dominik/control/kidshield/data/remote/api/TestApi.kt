package com.dominik.control.kidshield.data.remote.api

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface TestApi {

    @GET("api/v1/test")
    suspend fun open()

    @POST("api/v1/test")
    suspend fun closed()

    @DELETE("api/v1/test")
    suspend fun restricted()
}

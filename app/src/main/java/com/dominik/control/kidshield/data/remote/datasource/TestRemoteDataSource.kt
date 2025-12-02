package com.dominik.control.kidshield.data.remote.datasource

import com.dominik.control.kidshield.data.remote.api.TestApi
import jakarta.inject.Inject

class TestRemoteDataSource @Inject constructor(private val api: TestApi) {

    suspend fun open() = api.open()
    suspend fun closed() = api.closed()
    suspend fun restricted() = api.restricted()
}

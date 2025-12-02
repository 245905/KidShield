package com.dominik.control.kidshield.data.remote.datasource

import com.dominik.control.kidshield.data.model.domain.AppInfoEntity
import com.dominik.control.kidshield.data.model.dto.toDto
import com.dominik.control.kidshield.data.remote.api.AppInfoApi
import jakarta.inject.Inject

class AppInfoRemoteDataSource @Inject constructor(private val api: AppInfoApi) {

    suspend fun uploadData(data: List<AppInfoEntity>){
        val req = data.map { it.toDto() }
        api.uploadData(req)
    }
}

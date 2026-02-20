package com.dominik.control.kidshield.data.remote.datasource

import com.dominik.control.kidshield.data.model.domain.StepCountEntity
import com.dominik.control.kidshield.data.model.dto.toDto
import com.dominik.control.kidshield.data.model.dto.toEntity
import com.dominik.control.kidshield.data.remote.api.StepCountApi
import jakarta.inject.Inject

class StepCountRemoteDataSource @Inject constructor(private val api: StepCountApi) {

    suspend fun uploadData(data: List<StepCountEntity>){
        val req = data.map { it.toDto() }
        api.uploadData(req)
    }

    suspend fun downloadData(): List<StepCountEntity>{
        val req = api.downloadData()
        return req.map { it.toEntity() }
    }
}

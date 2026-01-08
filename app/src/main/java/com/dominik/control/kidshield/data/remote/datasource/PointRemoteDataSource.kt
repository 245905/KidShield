package com.dominik.control.kidshield.data.remote.datasource

import com.dominik.control.kidshield.data.model.domain.PointEntity
import com.dominik.control.kidshield.data.model.dto.toDto
import com.dominik.control.kidshield.data.model.dto.toEntity
import com.dominik.control.kidshield.data.remote.api.PointApi
import jakarta.inject.Inject

class PointRemoteDataSource @Inject constructor(private val api: PointApi)  {

    suspend fun uploadData(data: List<PointEntity>){
        val req = data.map { it.toDto() }
        api.uploadData(req)
    }

    suspend fun downloadData(): List<PointEntity>{
        val req = api.downloadData()
        return req.map { it.toEntity() }
    }
}

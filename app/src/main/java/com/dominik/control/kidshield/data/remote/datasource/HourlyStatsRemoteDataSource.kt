package com.dominik.control.kidshield.data.remote.datasource

import com.dominik.control.kidshield.data.model.domain.HourlyStatsEntity
import com.dominik.control.kidshield.data.model.dto.toDto
import com.dominik.control.kidshield.data.remote.api.HourlyStatsApi
import jakarta.inject.Inject

class HourlyStatsRemoteDataSource @Inject constructor(private val api: HourlyStatsApi) {

    suspend fun uploadData(data: List<HourlyStatsEntity>){
        val req = data.map { it.toDto() }
        api.uploadData(req)
    }

}

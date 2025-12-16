package com.dominik.control.kidshield.data.remote.datasource

import com.dominik.control.kidshield.data.model.domain.AppInfoDiffEntity
import com.dominik.control.kidshield.data.model.dto.toEntity
import com.dominik.control.kidshield.data.remote.api.AppInfoDiffApi
import jakarta.inject.Inject

class AppInfoDiffRemoteDataSource @Inject constructor(private val api: AppInfoDiffApi) {

    suspend fun downloadData(): List<AppInfoDiffEntity>{
        val req = api.downloadData()
        return req.map { it.toEntity() }
    }

}
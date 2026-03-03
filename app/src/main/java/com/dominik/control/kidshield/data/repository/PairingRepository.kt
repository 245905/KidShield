package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.model.Resource
import com.dominik.control.kidshield.data.model.dto.CheckPairStatusRequest
import com.dominik.control.kidshield.data.model.dto.CheckPairStatusResponse
import com.dominik.control.kidshield.data.model.dto.GenerateCodeResponse
import com.dominik.control.kidshield.data.model.dto.PairByPinRequest
import com.dominik.control.kidshield.data.model.dto.PairByUUIDRequest
import com.dominik.control.kidshield.data.remote.api.PairingApi
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface PairingRepository {
    suspend fun generateCode(): Resource<GenerateCodeResponse>
    suspend fun pairByPin(request: PairByPinRequest): Resource<Unit>
    suspend fun pairByUUID(request: PairByUUIDRequest): Resource<Unit>
    suspend fun checkPairingStatus(request: CheckPairStatusRequest): Resource<CheckPairStatusResponse>
}

class PairingRepositoryImpl @Inject constructor(
    private val pairingApi: PairingApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PairingRepository {

    override suspend fun generateCode(): Resource<GenerateCodeResponse> =
        withContext(ioDispatcher) {
            try {
                val resp = pairingApi.generateCode()
                Resource.Success(resp)
            } catch (e: Exception) {
                Resource.Error(e)
            }
        }

    override suspend fun pairByPin(request: PairByPinRequest): Resource<Unit> =
        withContext(ioDispatcher) {
            try {
                val resp = pairingApi.pairByPin(request)
                Resource.Success(resp)
            } catch (e: Exception) {
                Resource.Error(e)
            }
        }

    override suspend fun pairByUUID(request: PairByUUIDRequest): Resource<Unit> =
        withContext(ioDispatcher) {
            try {
                val resp = pairingApi.pairByUUID(request)
                Resource.Success(resp)
            } catch (e: Exception) {
                Resource.Error(e)
            }
        }

    override suspend fun checkPairingStatus(request: CheckPairStatusRequest): Resource<CheckPairStatusResponse> =
        withContext(ioDispatcher) {
            try {
                val resp = pairingApi.checkPairStatus(request)
                Resource.Success(resp)
            } catch (e: Exception) {
                Resource.Error(e)
            }
        }

}

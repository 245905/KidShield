package com.dominik.control.kidshield.data.repository

import com.dominik.control.kidshield.data.local.datasource.TokenRepository
import com.dominik.control.kidshield.data.model.Resource
import com.dominik.control.kidshield.data.model.dto.AuthResponse
import com.dominik.control.kidshield.data.model.dto.LoginRequest
import com.dominik.control.kidshield.data.model.dto.RefreshRequest
import com.dominik.control.kidshield.data.model.dto.RegisterRequest
import com.dominik.control.kidshield.data.remote.api.AuthApi
import com.dominik.control.kidshield.di.IoDispatcher
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException

interface AuthRepository {
    suspend fun login(request: LoginRequest): Resource<AuthResponse>
    suspend fun register(request: RegisterRequest): Resource<Unit>
    suspend fun logout(request: RefreshRequest): Resource<Unit>
    suspend fun refresh(request: RefreshRequest): Resource<Unit>
}

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStore: TokenRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Resource<AuthResponse> =
        withContext(ioDispatcher){
            try {
                val resp = authApi.login(request)
                tokenStore.saveTokens(resp.token, resp.refreshToken)
                Resource.Success(resp)
            } catch (e: HttpException){
                Resource.Error(e)
            } catch (e: IOException){
                Resource.Error(e)
            } catch (e: Exception){
                Resource.Error(e)
            }
        }

    override suspend fun register(request: RegisterRequest): Resource<Unit> =
        withContext(ioDispatcher) {
            try {
                authApi.register(request)
                Resource.Success(Unit)
            } catch (e: Exception) {
                Resource.Error(e)
            }
        }

    override suspend fun logout(request: RefreshRequest): Resource<Unit> =
        withContext(ioDispatcher) {
            try {
                authApi.logout(request)
                tokenStore.clearTokens()
                Resource.Success(Unit)
            } catch (e: Exception) {
                Resource.Error(e)
            }
        }

    override suspend fun refresh(request: RefreshRequest): Resource<Unit> =
        withContext(ioDispatcher) {
            try {
                val res = authApi.refreshToken(request)
                tokenStore.saveTokens(res.token, res.refreshToken)

                Resource.Success(Unit)
            } catch (e: Exception) {
                tokenStore.clearTokens()
                Resource.Error(e)
            }
        }

}

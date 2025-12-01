package com.dominik.control.kidshield.data.remote.retrofit

import com.dominik.control.kidshield.data.local.datasource.TokenRepository
import com.dominik.control.kidshield.data.model.dto.RefreshRequest
import com.dominik.control.kidshield.data.remote.api.AuthApi
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val apiServiceForAuth: AuthApi // ma endpoint refresh
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // synchronized refresh - prosty przykład
        synchronized(this) {
            val current = tokenRepository.getAccessToken()
            // jeśli żądanie już używało nowego tokenu -> nie próbujemy ponownie
            if (response.request.header("Authorization") != null &&
                response.request.header("Authorization") == "Bearer $current") {
                // refresh
                val refreshToken = tokenRepository.getRefreshToken() ?: return null
                val refreshResponse = runBlocking {
                    try {
                        apiServiceForAuth.refreshToken(RefreshRequest(refreshToken))
                    } catch (e: Exception) { null }
                }
                if (refreshResponse?.token != null) {
                    tokenRepository.saveTokens(refreshResponse.token, refreshResponse.refreshToken)
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer ${refreshResponse.token}")
                        .build()
                }
                return null
            }
            return null
        }
    }
}

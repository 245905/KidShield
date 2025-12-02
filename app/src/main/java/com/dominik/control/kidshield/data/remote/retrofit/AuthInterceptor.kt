package com.dominik.control.kidshield.data.remote.retrofit

import com.dominik.control.kidshield.data.local.datasource.TokenRepository
import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository // wrapper dla secure storage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenRepository.getAccessToken() // może być null
        val newReq = if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else request
        return chain.proceed(newReq)
    }
}

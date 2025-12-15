package com.dominik.control.kidshield.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.dominik.control.kidshield.data.local.datasource.TokenRepository
import com.dominik.control.kidshield.data.model.Resource
import com.dominik.control.kidshield.data.model.dto.RefreshRequest
import com.dominik.control.kidshield.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.withContext

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data object Authenticated : AuthState
}

@Singleton
class AuthManager @Inject constructor(
    private val context: Context,
    private val tokenStore: TokenRepository,
    private val authRepository: AuthRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state: StateFlow<AuthState> = _state

    private var started = false

    fun start() {
        if (started) return
        started = true
        CoroutineScope(ioDispatcher).launch {
            checkAuthOnStartup()
        }
    }

    private suspend fun checkAuthOnStartup() = withContext(ioDispatcher) {
        _state.value = AuthState.Loading

        val access = tokenStore.getAccessToken()
        val refresh = tokenStore.getRefreshToken()

        if (refresh.isNullOrBlank()) {
            _state.value = AuthState.Unauthenticated
            return@withContext
        }

        if (!hasInternet()) {
            // offline: jeśli miałeś tokens -> pozwól być online w trybie offline
            if (!access.isNullOrBlank()) {
                _state.value = AuthState.Authenticated
            } else {
                // brak access, ale jest refresh - nie mamy internetu, jednak lepiej dać offline experience?
                // decyzja: dajemy Authenticated bez access (możesz przekazać empty)
                _state.value = AuthState.Authenticated
            }
            return@withContext
        }

        // mamy internet -> jeśli access is present and valid (JWT check) -> ok
//        if (!access.isNullOrBlank() && isAccessTokenValid(access)) {
//            _state.value = AuthState.Authenticated
//            return@withContext
//        }

        // spróbuj refresh
        when(authRepository.refresh(RefreshRequest(refresh))){
            is Resource.Success -> {
                _state.value = AuthState.Authenticated
            }
            is Resource.Error -> {
                _state.value = AuthState.Unauthenticated
            }

            Resource.Loading -> {}
        }

    }

    suspend fun tryRefresh(): Boolean = withContext(ioDispatcher) {
        val refresh = tokenStore.getRefreshToken() ?: return@withContext false
//        val res = authRepository.refresh(RefreshRequest(refresh))
        when(val res = authRepository.refresh(RefreshRequest(refresh))){
            is Resource.Success -> {
                _state.value = AuthState.Authenticated
                return@withContext true
            }
            is Resource.Error -> {
                _state.value = AuthState.Unauthenticated
                return@withContext false
            }

            Resource.Loading -> {return@withContext false}
        }
    }

    fun forceAuthenticated(accessToken: String) {
        _state.value = AuthState.Authenticated
    }

    fun logout() {
        tokenStore.clearTokens()
        _state.value = AuthState.Unauthenticated
    }

    private fun hasInternet(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nc = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(nc) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun isAccessTokenValid(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return false
            val payload = parts[1]
            val decoded = String(android.util.Base64.decode(payload, android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP))
            val json = org.json.JSONObject(decoded)
            val exp = json.optLong("exp", 0L)
            exp != 0L && (System.currentTimeMillis() / 1000) < exp
        } catch (e: Exception) {
            false
        }
    }
}

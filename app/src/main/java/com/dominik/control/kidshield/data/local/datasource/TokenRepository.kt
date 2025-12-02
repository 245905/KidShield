package com.dominik.control.kidshield.data.local.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class TokenRepository @Inject constructor(
    private val prefs: SharedPreferences
) {

    companion object {
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
    }

    fun saveTokens(accessToken: String, refreshToken: String){
        prefs.edit() {
            putString(KEY_ACCESS, accessToken)
            putString(KEY_REFRESH, refreshToken)
        }
    }

    fun getAccessToken(): String?{
        return prefs.getString(KEY_ACCESS, null)
    }

    fun getRefreshToken(): String?{
        return prefs.getString(KEY_REFRESH, null)
    }

    fun clearTokens() {
        prefs.edit {
            remove(KEY_ACCESS)
            remove(KEY_REFRESH)
        }
    }

}

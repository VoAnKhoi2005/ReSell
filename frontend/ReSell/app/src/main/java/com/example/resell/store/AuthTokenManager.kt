package com.example.resell.store

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import com.example.resell.model.AuthToken

@Singleton
class AuthTokenManager @Inject constructor(
    private val sharedPrefs: SharedPreferences
){
    fun saveToken(token: AuthToken) {
        sharedPrefs.edit { putString("jwt_access_token", token.accessToken) }
        sharedPrefs.edit { putString("jwt_refresh_token", token.refreshToken) }
    }

    fun getAccessToken(): String? {
        return sharedPrefs.getString("jwt_access_token", null)
    }

    fun getRefreshToken(): String? {
        return sharedPrefs.getString("jwt_refresh_token", null)
    }

    fun clearToken() {
        sharedPrefs.edit { clear() }
    }
}
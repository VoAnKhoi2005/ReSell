package store

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import model.AuthToken

@Singleton
class TokenManager @Inject constructor(
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
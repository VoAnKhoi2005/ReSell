package store

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import model.AuthToken

@Singleton
class TokenManager @Inject constructor(@ApplicationContext private val context: Context){
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: AuthToken) {
        prefs.edit { putString("jwt_access_token", token.accessToken) }
        prefs.edit { putString("jwt_refresh_token", token.refreshToken) }
    }

    fun getAccessToken(): String? {
        return prefs.getString("jwt_access_token", null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString("jwt_refresh_token", null)
    }

    fun clearToken() {
        prefs.edit { clear() }
    }
}
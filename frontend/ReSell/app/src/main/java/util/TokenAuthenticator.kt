package util

import com.example.resell.ui.network.RefreshApiService
import model.AuthToken
import model.RefreshRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import store.AuthTokenManager
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: AuthTokenManager,
    private val refreshApiService: RefreshApiService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // Avoid retry loops
        if (responseCount(response) >= 2) return null

        val refreshToken = tokenManager.getRefreshToken() ?: return null

        val refreshResponse = try {
            refreshApiService.refreshSession(RefreshRequest(refreshToken)).execute()
        } catch (e: Exception) {
            return null
        }

        if (refreshResponse.isSuccessful) {
            val newAccessToken = refreshResponse.body()?.accessToken ?: return null
            val newRefreshToken = refreshResponse.body()?.refreshToken ?: return null
            tokenManager.saveToken(AuthToken(newAccessToken, newRefreshToken))

            // Retry original request with new token
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }

        return null
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}

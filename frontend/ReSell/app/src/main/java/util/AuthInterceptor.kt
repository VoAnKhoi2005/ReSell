package util

import okhttp3.Interceptor
import okhttp3.Response
import store.AuthTokenManager
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: AuthTokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getAccessToken()
        val requestBuilder = chain.request().newBuilder()

        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}

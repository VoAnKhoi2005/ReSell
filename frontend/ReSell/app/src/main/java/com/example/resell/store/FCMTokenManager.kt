package com.example.resell.store

import android.util.Log
import arrow.core.Either
import com.example.resell.model.Notification
import com.example.resell.network.ApiService
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.resell.model.SaveFCMTokenRequest
import com.example.resell.network.NetworkError
import com.example.resell.network.toNetworkError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMTokenManager @Inject constructor(
    private val apiService: ApiService
) {
    fun fetchAndSendToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM Manager", "Fetching token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM", "FCM Token: $token")

            CoroutineScope(Dispatchers.IO).launch {
                val response = saveFCMToken(token)
                response.fold(
                    ifLeft = { error ->
                        Log.e("FCM Manager", "Fail to save FCM token: " + error.message)
                    },
                    ifRight = {
                        Log.d("FCM Manager", "Success saving FCM token")
                    }
                )
            }
        }
    }

    private suspend fun saveFCMToken(token: String): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.saveFCMToken(SaveFCMTokenRequest(token))
            true
        }.mapLeft { it.toNetworkError() }
    }

    suspend fun deleteFCMToken(): Either<NetworkError, Boolean> {
        return Either.catch {
            apiService.deleteFCMToken()
            true
        }.mapLeft { it.toNetworkError() }
    }
}
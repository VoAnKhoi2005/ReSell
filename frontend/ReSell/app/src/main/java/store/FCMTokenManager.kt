package store

import android.util.Log
import com.example.resell.ui.network.ApiService
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.SaveFCMTokenRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMTokenManager @Inject constructor(
    private val apiService: ApiService
) {
    fun fetchAndSendToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching token failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result
                Log.d("FCM", "FCM Token: $token")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        apiService.saveFCMToken(SaveFCMTokenRequest(token))
                    } catch (e: Exception) {
                        Log.e("FCM", "Failed to send token", e)
                    }
                }
            }

    }
}
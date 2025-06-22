package com.example.resell.store

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class AppMessagingService : FirebaseMessagingService() {

    @Inject lateinit var fcmTokenManager: FCMTokenManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        fcmTokenManager.fetchAndSendToken()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title.orEmpty()
        val body = message.notification?.body.orEmpty()

        val type = message.data["type"] ?: ""
        val notificationID = message.data["notification_id"] ?: ""

        showNotification(title, body, type, notificationID)
    }

    private fun showNotification(title: String, message: String, type: String, notificationID: String) {
        //TODO("Tự làm đi nha. Phân loại kèm UI j đó tùy các ông. Loại thì ở dưới đó hoặc tự vô model coi")
        //enum class com.example.resell.model.NotificationType{
        //    message, alert, system, reminder
        //}
    }
}
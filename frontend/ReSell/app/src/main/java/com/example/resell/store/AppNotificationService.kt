package com.example.resell.store

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.resell.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class AppNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenManager: FCMTokenManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        fcmTokenManager.fetchAndSendToken()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title.orEmpty()
        val body = message.notification?.body.orEmpty()

        val type = message.data["type"] ?: "default"
        val notificationID = message.data["notification_id"] ?: "default_id"

        showNotification(title, body, type, notificationID)
    }

    private fun showNotification(title: String, message: String, type: String, notificationID: String) {
        val channelId = "resell_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "ReSell App Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val iconRes = R.drawable.ic_launcher_foreground
        val accentColor = android.graphics.Color.DKGRAY

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(accentColor)
            .setAutoCancel(true)
            .build()

        val id = notificationID.hashCode()
        notificationManager.notify(id, notification)
    }
}

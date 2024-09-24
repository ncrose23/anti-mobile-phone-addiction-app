package com.mobilesecurity.antimobileapp.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mobilesecurity.antimobileapp.R

class NotificationsModel() {
    // define notification configuration
    companion object {
        private var CHANNEL_ID = "screen-time-notification-channel"
        private var NOTIFICATION_ID = 1
        private var CHANNEL_NAME = "Screen Time Notifications"
        private var requestCode = 1
        var channelIsCreated = false

        val NOTIFICATIONS_TITLE_EXTRA = "notifications-title"
        val NOTIFICATIONS_BODY_EXTRA = "notificationsbody"

        fun getRequestCode(): Int {
            return requestCode++
        }

        fun getIntentToLaunchBroadcast(context: Context, title: String, body: String): Intent {
            val intent = Intent(context, NotificationsReceiver::class.java)
            intent.putExtra(NOTIFICATIONS_TITLE_EXTRA, title)
            intent.putExtra(NOTIFICATIONS_BODY_EXTRA, body)
            return intent
        }

        fun createNotification(context: Context, title: String, body: String) {
            if (!channelIsCreated) {
                createNotificationsChannel(context)
            }
            // build notification
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .build()

            Log.d("notification", "notification created")
            // send notification
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }

        fun createNotificationsChannel(context: Context) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            channelIsCreated = true
        }
    }






}
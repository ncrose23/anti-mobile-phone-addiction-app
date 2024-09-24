package com.mobilesecurity.antimobileapp.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmManagerModel(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE)
            as AlarmManager

        private var pendingIntent: PendingIntent? = null
    public var isPolling = false
        private  set


        private fun getPendingIntent(): PendingIntent {
            val sharedPref = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)
            val maxTimeInMinutesBeforeSendingNotification =
                sharedPref.getInt("maxTimeInMinutesBeforeSendingNotification", 1)


            // convenience method to get an intent showing the notification
            val intent = NotificationsModel.getIntentToLaunchBroadcast(
                context,
                "Screen Time Notification",
                "You have spent $maxTimeInMinutesBeforeSendingNotification minutes on the screen. Get off you bum")
            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }


        fun setRepeatingAlarm() {
            this.pendingIntent = getPendingIntent()
            isPolling = false

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 1000,
                60000,
                pendingIntent!!
            )
        }



    fun setPollingAlarm() {
        this.pendingIntent = getPendingIntent()
        isPolling = true

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 1000,
            AlarmManager.INTERVAL_DAY,
            pendingIntent!!
        )
        Log.d("AlarmManagerModel", "setting polling alarm")
    }

    fun cancelPollingAlarm() {
        cancelAlarm()
        isPolling = false
        Log.d("AlarmManagerModel", "cancelling polling alarm")
    }

        fun cancelAlarm() {
            Log.d("AlarmManagerModel", "cancelling alarm")
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent!!)
            }
        }


}
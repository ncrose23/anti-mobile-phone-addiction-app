package com.mobilesecurity.antimobileapp.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mobilesecurity.antimobileapp.storage.StorageModel

class NotificationsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        // get extras from intent
        if (intent == null || context == null) {
            return
        }

        val title = intent.getStringExtra(NotificationsModel.NOTIFICATIONS_TITLE_EXTRA)!!
        val body = intent.getStringExtra(NotificationsModel.NOTIFICATIONS_BODY_EXTRA)!!

        val sharedPref = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)
        val elapsedTime = sharedPref.getLong("screenOnTime", 0)
        val maxTimeInMinutesBeforeSendingNotification =
            sharedPref.getInt("maxTimeInMinutesBeforeSendingNotification", 1)
        Log.d(
            "NotificationsReceiver",
            "elapsedTime: $elapsedTime, alotted time: $maxTimeInMinutesBeforeSendingNotification minutes"
        )

        // if the time spent on the screen is less than the time set in the settings, do nothing
        if (elapsedTime < maxTimeInMinutesBeforeSendingNotification * 60 * 1000) {
            Log.d(
                "NotificationsReceiver",
                "Time spent on the screen is less than the time set in the settings"
            )
            return
        }


        // TODO("create notification here?")
        val alarmManagerModel = AlarmManagerModel(context)


        // as soon as 1 day alarm activates, cancel it and instead activate 1 minute alarm
        if (StorageModel.getIsRepeatingNotificationsAlarmPolling(context)) {
            alarmManagerModel.cancelPollingAlarm()
            alarmManagerModel.setRepeatingAlarm()
            StorageModel.setIsRepeatingNotificationsAlarmPolling(context, false)
        }
        else {
            // cancel 1 minute alarm, and immediately enable 1 day alarm
            alarmManagerModel.cancelAlarm()

            // set polling alarm
            alarmManagerModel.setPollingAlarm()
            StorageModel.setIsRepeatingNotificationsAlarmPolling(context, true)
        }

        // create notification regardless
        NotificationsModel.createNotification(context, title, body)
    }
}
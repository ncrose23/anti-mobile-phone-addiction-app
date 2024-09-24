package com.mobilesecurity.antimobileapp.storage

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mobilesecurity.antimobileapp.notifications.NotificationsModel
import java.util.Calendar

class StorageModel(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE)
            as AlarmManager

    private fun setShouldHaveClearStorageAlarm(boolean: Boolean) {
        val sharedPref = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("clearStorageAlarmIsOn", boolean)
        editor.apply()
    }

     fun isClearStorageAlarmTurnedOn(): Boolean {
        val sharedPref = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("clearStorageAlarmIsOn", false)
    }

    fun setRepeatingClearStorageAlarm() {
        if (isClearStorageAlarmTurnedOn()) {
            Log.d("StorageModel", "clear storage alarm already set.")
            val clock = alarmManager.nextAlarmClock
            Log.d("StorageModel", "next alarm clock is set to ${clock?.triggerTime}")
            alarmManager.nextAlarmClock?.let {
                Log.d("StorageModel", "next alarm clock is set to ${it.triggerTime}")
            }
            return
        }
        val intent = Intent(context, StorageReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            17,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("StorageModel", "setting repeating alarm")
        val calendar = Calendar.getInstance().also { cal ->
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.add(Calendar.DATE, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        this.setShouldHaveClearStorageAlarm(true)
    }

    companion object {

        fun setIsRepeatingNotificationsAlarmPolling(context: Context, boolean: Boolean) {
            val sharedPref = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("isRepeatingNotificationsAlarmPolling", boolean)
            editor.apply()
        }

        fun getIsRepeatingNotificationsAlarmPolling(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)
            return sharedPref.getBoolean("isRepeatingNotificationsAlarmPolling", false)
        }

        fun getUserScreenAlertTime(context: Context): Int {
            val sharedPref = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)
            val maxTimeInMinutesBeforeSendingNotification =
                sharedPref.getInt("maxTimeInMinutesBeforeSendingNotification", 1)
            return maxTimeInMinutesBeforeSendingNotification
        }

        fun setUserScreenAlertTime(context: Context, numMinutes: Int) {
            Log.d("StorageModel", "setting user screen alert time to $numMinutes minutes")
            val sharedPref = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putInt("maxTimeInMinutesBeforeSendingNotification", numMinutes)
            editor.apply()
        }

        fun getScreenTime(context: Context): Long {
            val sharedPreferences = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)

            // get stored time first
            val storedTime = sharedPreferences.getLong("screenOnTime", 0)
            return storedTime
        }

        fun clearScreenTime(context: Context) {
            val sharedPreferences = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putLong("screenOnTime", 0)
            editor.apply()
        }

        fun setRepeatingNotificationsStatus(context: Context, isTurnedOn: Boolean) {
            val sharedPreferences = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("shouldSendRepeatingNotifications", isTurnedOn)
            editor.apply()
        }

        fun getRepeatingNotificationsStatus(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("shouldSendRepeatingNotifications", false)
        }
    }
}
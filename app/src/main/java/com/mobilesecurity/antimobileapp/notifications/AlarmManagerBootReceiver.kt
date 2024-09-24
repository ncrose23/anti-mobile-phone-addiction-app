package com.mobilesecurity.antimobileapp.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mobilesecurity.antimobileapp.storage.StorageModel

class AlarmManagerBootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "in onReceive")
        if (context == null || intent == null) {
            return
        }
        val storageModel = StorageModel(context)
        if (storageModel.isClearStorageAlarmTurnedOn()) {
            storageModel.setRepeatingClearStorageAlarm()
        }

        val alarmManagerModel = AlarmManagerModel(context)
        if (StorageModel.getRepeatingNotificationsStatus(context)) {
            if (StorageModel.getIsRepeatingNotificationsAlarmPolling(context)) {
                alarmManagerModel.setPollingAlarm()
            } else {
                alarmManagerModel.setRepeatingAlarm()
            }
        }
    }

    companion object {
        val TAG = "AlarmManagerBootReceiver"
    }
}
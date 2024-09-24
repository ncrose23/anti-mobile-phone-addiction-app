package com.mobilesecurity.antimobileapp.storage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class StorageReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) {
            return
        }

        Log.d("StorageReceiver", "clearing storage")

        // once every day, reset screen time
        StorageModel.clearScreenTime(context)

    }
}
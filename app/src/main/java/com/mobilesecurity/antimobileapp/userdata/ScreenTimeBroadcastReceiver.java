package com.mobilesecurity.antimobileapp.userdata;

import static android.os.Build.VERSION_CODES.P;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

public class ScreenTimeBroadcastReceiver extends BroadcastReceiver {

    private long startTimer = System.currentTimeMillis();
    private long screenOnTime;


    public void onReceive(Context context, Intent intent) {
        Log.i("ScreenTimeBroadcastReceiver", "ScreenTimeService onReceive");
        if (intent == null || context == null)
            return;
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            startTimer = System.currentTimeMillis();

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("ScreenTime", Context.MODE_PRIVATE);
            // get stored time first
            long storedTime = sharedPreferences.getLong("screenOnTime", 0);
            Log.d("ScreenTimeBroadcastReceiver", "Stored time: " + storedTime);

            long endTimer = System.currentTimeMillis();
            long screenOnTimeSingle = endTimer - startTimer;
            screenOnTime += screenOnTimeSingle;

            // store new screen time in shared preferences
            sharedPreferences.edit()
                    .putLong("screenOnTime", screenOnTimeSingle+storedTime)
                    .apply();

            System.out.println(screenOnTimeSingle);
        }
    }
}
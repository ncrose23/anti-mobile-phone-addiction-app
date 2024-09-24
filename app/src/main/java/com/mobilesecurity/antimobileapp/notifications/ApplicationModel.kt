package com.mobilesecurity.antimobileapp.notifications

import android.app.Application

class ApplicationModel : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationsModel.createNotificationsChannel(this)

    }
}
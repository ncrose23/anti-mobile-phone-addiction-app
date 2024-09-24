package com.mobilesecurity.antimobileapp.notifications


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity

class NotificationPermissionRequester(private val context: Activity) {

    fun requestPermission() {
        Log.d("request-permission", "requesting")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted, you can now send notifications
            Toast.makeText(context, "Permission already granted", Toast.LENGTH_SHORT).show()
        } else {
            // Request the permission
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }

    fun checkPermissionIsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    }
}
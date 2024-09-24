package com.mobilesecurity.antimobileapp.notifications



import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.ViewModel

class NotificationsPermissionRequesterViewModel : ViewModel() {
    public var permission = android.Manifest.permission.POST_NOTIFICATIONS
        private set
    private var launcher: ActivityResultLauncher<String>? = null

    // this is how you do state in viewmodels
    public var isGranted by mutableStateOf(false)
        private set
    public var shouldShowPermissionsRationale by mutableStateOf(false)
        private set


    fun setPermission(permission: String) {
        this.permission = permission
    }

    fun getPermission(context: Context): Boolean {
        isGranted = ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        return isGranted
    }


    @Composable
    fun CreateLauncher(callback: () -> Unit = {}) {
        launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { permissionIsGranted ->
                Log.d("permissions", "permission from launcher: $permissionIsGranted")
                isGranted = permissionIsGranted
                if (!permissionIsGranted) {
                    Log.d("permissions", "permission is not granted")
                    shouldShowPermissionsRationale = true
                    callback()
                }
            }
        )
    }

    fun requestPermission() {
        if (launcher == null) {
            throw IllegalStateException("Launcher is not initialized")
        }
        launcher?.launch(permission)
    }


    fun onDismissDialog() {
        shouldShowPermissionsRationale = false
    }

    fun onConfirmDialog() {
        shouldShowPermissionsRationale = false
        requestPermission()
    }

    companion object {
        fun openAppSettings(context: Activity) {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    @Composable
    fun ShowDialog(context: Activity) {
        if (shouldShowPermissionsRationale) {
            PermissionsDialog(
                title = "Permission to send notifications",
                body = "We need permission to send notifications to you to let you know " +
                        "when you're over your screen time. " +
                        "Please allow this permission in the settings.",
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(context, permission),
                onDismiss = { onDismissDialog() },
                onConfirmPermission = { onConfirmDialog() },
                onGoToAppSettingsClick = {
                    openAppSettings(
                        context
                    )
                }
            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsDialog(
    title: String,
    body: String,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onConfirmPermission: () -> Unit,
    onGoToAppSettingsClick: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onDismiss()
                if (isPermanentlyDeclined) {
                    onGoToAppSettingsClick()
                } else {
                    onConfirmPermission()
                }
            }) {
                Text("Allow")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Deny")
            }
        },
        title = {
            Text(title)
        },
        text = {
            Text(body)
        }
    )
}
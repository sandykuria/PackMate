package com.example.packmate

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.packmate.ui.theme.PackMateTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.messaging
import com.google.firebase.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            PackMateTheme {
                val viewModel: PackViewModel = viewModel()
                // Main screen content
                Greeting()
                PackMateScreen(viewModel)
            }
        }
        retrieveToken()
    }

    private fun retrieveToken() {
        Firebase.messaging.token.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result
                Log.d("FCM token", token)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val openDialog = remember { mutableStateOf(false) }

    val notificationPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.POST_NOTIFICATIONS
    )

    // Show dialog if permission is not granted
    if (openDialog.value) {
        RequestNotificationPermissionDialog(
            openDialog = openDialog,
            permissionState = notificationPermissionState
        )
    }

    // Handle side-effects
    LaunchedEffect(Unit) {
        if (notificationPermissionState.status.isGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Permission already granted or not needed
            Firebase.messaging.subscribeToTopic("Tutorial")
        } else {
            // Ask user for notification permission
            openDialog.value = true
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Firebase Cloud Messaging Example")
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionDialog(
    openDialog: androidx.compose.runtime.MutableState<Boolean>,
    permissionState: com.google.accompanist.permissions.PermissionState
) {
    AlertDialog(
        onDismissRequest = { openDialog.value = false },
        title = { Text(text = "Notification Permission") },
        text = { Text(text = "This app needs notification permission to receive important updates.") },
        confirmButton = {
            Button(
                onClick = {
                    openDialog.value = false
                    permissionState.launchPermissionRequest()
                }
            ) {
                Text("Allow")
            }
        },
        dismissButton = {
            TextButton(onClick = { openDialog.value = false }) {
                Text("Cancel")
            }
        }
    )
}

package com.example.packmate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

const val TAG = "FCM"

class MessagingService : FirebaseMessagingService(){


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed Token: $token")
        //Update token on server
    }

    override fun onMessageReceived(message: RemoteMessage){
        super.onMessageReceived(message)

        //Check if message contains a notification payload
        message.notification?.let {
            showNotification(it)
        }

        //Check if message contains a data payload
        if (message.data.isNotEmpty()) {
            handleDataMessage()
        }

    }

    private fun handleDataMessage() {
        Log.d(TAG, "handleDataMessage")
    }

    fun showNotification(message: RemoteMessage.Notification){

        val intent = Intent(this, MainActivity::class.java).apply{
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(this, 0 ,intent, PendingIntent.FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)

        val channelId = "Default"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelName = "Firebase Messaging"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        manager.notify(Random.nextInt(), notificationBuilder)


    }


}

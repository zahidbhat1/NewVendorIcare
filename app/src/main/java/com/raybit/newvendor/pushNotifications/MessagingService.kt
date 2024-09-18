package com.raybit.newvendor.pushNotifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.raybit.newvendor.R
import java.util.*
import javax.inject.Inject

/**
 * Created by Aamir Bashir on 24-05-2021.
 */
class MessagingService @Inject constructor(
) : FirebaseMessagingService() {
    private val TAG: String="NOTIFICATION:: "

    override fun onMessageReceived(remoteMessage: RemoteMessage) {


        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            handleNow(remoteMessage.data)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

    }

    private fun handleNow(notificationData: MutableMap<String, String>) {
        val notificationBuilder = NotificationCompat.Builder(this,getString(R.string.default_notification_channel_id) )
            .setContentTitle(notificationData.get("title")) //Header
            .setContentText(notificationData.get("body")) //Content
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 1000))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
            notificationBuilder.color = ContextCompat.getColor(this, R.color.colorAccent)
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                getString(R.string.default_notification_channel_id), getText(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.lightColor = Color.GRAY
            mChannel.enableLights(true)

            notificationManager.createNotificationChannel(mChannel)
        }

        notificationManager.notify( Calendar.getInstance().timeInMillis.toInt(), notificationBuilder.build())
    }
}
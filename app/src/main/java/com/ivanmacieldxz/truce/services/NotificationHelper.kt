package com.ivanmacieldxz.truce.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ivanmacieldxz.truce.MainActivity
import com.ivanmacieldxz.truce.R

object NotificationHelper {

    const val CHANNEL_ID_FRIENDSHIPS = "truce_friendships"
    const val CHANNEL_ID_TIME_REQUESTS = "truce_time_requests"
    const val CHANNEL_ID_GENERAL = "truce_general"

    const val EXTRA_NAV_TAB = "extra_nav_tab"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val friendshipsChannel = NotificationChannel(
                CHANNEL_ID_FRIENDSHIPS,
                "Solicitudes de amistad",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de solicitudes de amistad recibidas y aceptadas"
                enableLights(true)
                enableVibration(true)
            }

            val timeRequestsChannel = NotificationChannel(
                CHANNEL_ID_TIME_REQUESTS,
                "Solicitudes de tiempo",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de peticiones de tiempo extra de amigos"
                enableLights(true)
                enableVibration(true)
            }

            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones generales de la aplicación Truce"
            }

            notificationManager.createNotificationChannels(
                listOf(friendshipsChannel, timeRequestsChannel, generalChannel)
            )
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        body: String,
        channelId: String = CHANNEL_ID_GENERAL,
        navTab: String? = null,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        createNotificationChannels(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (!navTab.isNullOrBlank()) {
                putExtra(EXTRA_NAV_TAB, navTab)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (ignored: SecurityException) {
            // Permission revoked or not granted
        }
    }
}

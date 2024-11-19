package com.example.serviso_app

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class BookingNotificationService : Service() {
    val CHANNEL_ID = "BookingNotificationChannel"
    private val binder = BookingNotificationBinder()

    inner class BookingNotificationBinder : Binder() {
        fun getService(): BookingNotificationService = this@BookingNotificationService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()  // Ensure the channel is created when the service is created
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Booking Notifications"
            val descriptionText = "Notifications for booking confirmations"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("NotificationPermission")
    fun sendNotification(booking: Booking) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create your notification

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Booking Confirmed")
            .setContentText("You've booked ${booking.serviceName} with ${booking.providerName}. Price: $${booking.price}")
            .setSmallIcon(R.drawable.baseline_book_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}





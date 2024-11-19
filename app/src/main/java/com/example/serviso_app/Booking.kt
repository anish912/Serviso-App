package com.example.serviso_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serviceName: String,
    val providerName: String,
    val bookingDate: String,
    val bookingTime: String,
    val price: Double,
    val location: String
)

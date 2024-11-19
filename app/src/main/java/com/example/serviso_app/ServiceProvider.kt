package com.example.serviso_app

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "service_providers")
data class ServiceProvider(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serviceId: Int, // Foreign Key to Service
    val name: String,
    val imageResId: Int,
    val rating: Float,
    val ratePerHour: Double,
    val phoneNumber: String
):Serializable

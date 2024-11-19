package com.example.serviso_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class Service(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int, // Foreign Key to Category
    val name: String,
    val description: String

)

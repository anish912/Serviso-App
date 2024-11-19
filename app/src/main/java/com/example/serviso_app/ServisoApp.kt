package com.example.serviso_app

import android.app.Application
import androidx.room.Room

class ServisoApp : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "serviso-db"
        )

            .build()
    }
}
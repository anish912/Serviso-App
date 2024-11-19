package com.example.serviso_app

import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [Category::class,Service::class,ServiceProvider::class,Booking::class], version = 1)
abstract class AppDatabase :RoomDatabase(){
    abstract fun categoryDao(): CategoryDao
    abstract fun serviceDao(): ServiceDao
    abstract fun serviceProviderDao(): ServiceProviderDao
    abstract fun bookingDao(): BookingDao

}
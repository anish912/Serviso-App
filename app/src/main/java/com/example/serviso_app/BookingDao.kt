package com.example.serviso_app

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookingDao {

    @Insert
    suspend fun insertBooking(booking: Booking)

    @Query("SELECT * FROM bookings ORDER BY bookingTime DESC")
    fun getAllBookings(): LiveData<List<Booking>>

}

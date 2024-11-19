package com.example.serviso_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RequestsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requests)

        recyclerView = findViewById(R.id.recyclerViewBookings)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val bookingDao = (application as ServisoApp).database.bookingDao()


        bookingDao.getAllBookings().observe(this, Observer { bookings ->
            bookingAdapter = BookingAdapter(bookings)
            recyclerView.adapter = bookingAdapter
        })

    }
}
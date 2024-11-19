package com.example.serviso_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceName: TextView = itemView.findViewById(R.id.tvServiceName)
        val providerName: TextView = itemView.findViewById(R.id.tvProviderName)
        val bookingDetails: TextView = itemView.findViewById(R.id.tvBookingDetails)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.serviceName.text = booking.serviceName
        holder.providerName.text = "Provider: ${booking.providerName}"
        holder.bookingDetails.text = "Date: ${booking.bookingDate}, Time: ${booking.bookingTime}"
        holder.price.text = "Price: $${booking.price}"
    }

    override fun getItemCount(): Int = bookings.size
}

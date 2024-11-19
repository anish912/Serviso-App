package com.example.serviso_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServiceProviderAdapter(
    private val onServiceProviderClick: (ServiceProvider) -> Unit
) : RecyclerView.Adapter<ServiceProviderAdapter.ServiceProviderViewHolder>() {

    private val serviceProviders = mutableListOf<ServiceProvider>()

    fun submitList(newServiceProviders: List<ServiceProvider>) {
        serviceProviders.clear()
        serviceProviders.addAll(newServiceProviders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceProviderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_provider, parent, false)
        return ServiceProviderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceProviderViewHolder, position: Int) {
        val serviceProvider = serviceProviders[position]
        holder.bind(serviceProvider)
    }

    override fun getItemCount(): Int = serviceProviders.size

    inner class ServiceProviderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val providerImage: ImageView = itemView.findViewById(R.id.providerImage)
        private val providerName: TextView = itemView.findViewById(R.id.providerName)
        private val providerRatePerHour: TextView = itemView.findViewById(R.id.ratePerHour)
        private val providerRatingText: TextView = itemView.findViewById(R.id.providerRatingText)

        fun bind(provider: ServiceProvider) {
            providerName.text = provider.name
            providerRatePerHour.text = "Rate: $${provider.ratePerHour}/hr"
            providerRatingText.text = provider.rating.toString()
            providerImage.setImageResource(provider.imageResId)

            // Set the star icon based on the rating
            val starIcon = if (provider.rating >= 4.5) {
                R.drawable.ic_star // Solid star icon for high rating
            } else {
                R.drawable.ic_star_border // Outline star icon for lower rating
            }
            providerRatingText.setCompoundDrawablesWithIntrinsicBounds(starIcon, 0, 0, 0)

            itemView.setOnClickListener {
                onServiceProviderClick(provider)
            }
        }
    }
}

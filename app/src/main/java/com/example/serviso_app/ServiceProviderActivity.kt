package com.example.serviso_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ServiceProviderActivity : AppCompatActivity() {


    private lateinit var serviceNameTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceProviderAdapter: ServiceProviderAdapter
    private lateinit var serviceProviderDao: ServiceProviderDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_provider)



        serviceNameTextView = findViewById(R.id.textServiceName)
        recyclerView = findViewById(R.id.recyclerViewServiceProviders)

        // Retrieve data passed from the previous activity
        val serviceId = intent.getIntExtra("serviceId", -1)
        val serviceName = intent.getStringExtra("serviceName")

        // Set service name in the TextView
        serviceNameTextView.text = serviceName

        // Initialize the database DAO and adapter
        serviceProviderDao = (application as ServisoApp).database.serviceProviderDao()
        serviceProviderAdapter = ServiceProviderAdapter{serviceProvider ->
            val intent= Intent(this,ProviderDetailActivity::class.java).apply {
                putExtra("provider",serviceProvider)

            }
            startActivity(intent)

        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = serviceProviderAdapter

        // Observe the list of service providers for the selected service
        serviceProviderDao.getServiceProvidersByServiceId(serviceId).observe(this, Observer { providers ->
            serviceProviderAdapter.submitList(providers)
        })


    }
}

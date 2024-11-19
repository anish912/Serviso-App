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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryDetailsActivity : AppCompatActivity() {

    private lateinit var categoryLogo: ImageView
    private lateinit var categoryName: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceDao: ServiceDao
    private lateinit var serviceAdapter: ServiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_details)

        categoryLogo = findViewById(R.id.categoryLogo)
        categoryName = findViewById(R.id.categoryName)
        recyclerView = findViewById(R.id.recyclerViewServices)

        val categoryId = intent.getIntExtra("categoryId", -1)
        val categoryNameText = intent.getStringExtra("categoryName")
        val categoryLogoResId = intent.getIntExtra("categoryImg", -1)

        categoryName.text=categoryNameText
        categoryLogo.setImageResource(categoryLogoResId)
        serviceDao = (application as ServisoApp).database.serviceDao()

        recyclerView.layoutManager = LinearLayoutManager(this)
        serviceAdapter = ServiceAdapter { service ->
            Toast.makeText(this,"You have selected: ${service.name}",Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ServiceProviderActivity::class.java).apply {
                putExtra("serviceId", service.id)
                putExtra("serviceName", service.name)


            }
            startActivity(intent)


        }
        recyclerView.adapter = serviceAdapter

        serviceDao.getServicesByCategory(categoryId).observe(this) { services ->
            serviceAdapter.submitList(services)
        }


    }
}
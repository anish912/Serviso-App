package com.example.serviso_app


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var editTextSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var textLocation: TextView
    private lateinit var textFetchLocation: TextView
    private lateinit var db: AppDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var serviceDao: ServiceDao
    private lateinit var serviceProviderDao: ServiceProviderDao
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextSearch=findViewById(R.id.editTextSearch)
        fab=findViewById(R.id.fab)
        textLocation = findViewById(R.id.textLocation)
        textFetchLocation = findViewById(R.id.textFetchingLocation)
        recyclerView = findViewById(R.id.recyclerViewServices)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fetchLocation()




        db = Room.databaseBuilder(applicationContext,AppDatabase::class.java,"serviso-db").build()
        categoryDao = db.categoryDao()
        serviceDao = db.serviceDao()
        serviceProviderDao = db.serviceProviderDao()
        prepopulateDatabase()
        fab.setOnClickListener {
           val query= editTextSearch.text.toString()
            if (query.isNotEmpty()){
                searchService(query)
            }
            else{
                Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show()
            }
        }






        recyclerView.setHasFixedSize(true)
        val gm=GridLayoutManager(this,3)
        gm.orientation=GridLayoutManager.VERTICAL
        recyclerView.layoutManager=gm

        categoryDao.getAllCategories().observe(this){categories->

            recyclerView.adapter=CategoryAdapter(categories){category ->
                Toast.makeText(this, "You Have Selected: ${category.name}", Toast.LENGTH_LONG).show()
                val intent=Intent(this,CategoryDetailsActivity::class.java).apply {
                    putExtra("categoryId",category.id)
                    putExtra("categoryName",category.name)
                    putExtra("categoryImg",category.img)
                }
                startActivity(intent)

            }

        }

        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_requests -> {
                    Toast.makeText(this, "Requests Selected", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, RequestsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_notifications -> {
                    Toast.makeText(this, "Notifications Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_categories -> {
                    Toast.makeText(this, "Categories Selected", Toast.LENGTH_SHORT).show()

                    val currentActivity = this.javaClass.simpleName
                    if (currentActivity != "MainActivity") {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    true

                }
                R.id.nav_chat -> {
                    Toast.makeText(this, "Chat Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_account -> {
                    Toast.makeText(this, "Account Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false

            }
        }




    }



    private fun fetchLocation(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener{ location ->
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val state = address.adminArea
                        val country = address.countryName
                        textFetchLocation.text = "$state, $country"
                    }
                }
                else{
                    Toast.makeText(this,"Failed on getting current Location",Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener {
                textFetchLocation.text = "Failed to get location"
            }
            .addOnSuccessListener {
                Toast.makeText(this,"yay we got the location",Toast.LENGTH_SHORT)
                    .show()
            }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_PERMISSION_REQUEST_CODE->{
                if (grantResults.isNotEmpty() &&
                    grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    fetchLocation()

                }
                else{
                    Toast.makeText(this,"You need to grant permission to access location",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
    private fun searchService(serviceName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Fetch the service by name asynchronously
                val service = serviceDao.getServiceByName(serviceName)
                withContext(Dispatchers.Main) {
                    if (service != null) {
                        // Navigate to the ServiceProviderActivity
                        val intent = Intent(this@MainActivity, ServiceProviderActivity::class.java).apply {
                            putExtra("serviceId", service.id)
                            putExtra("serviceName", service.name)
                        }
                        startActivity(intent)
                    } else {
                        // Show a message if the service is not found
                        Toast.makeText(this@MainActivity, "Service not found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun prepopulateDatabase() {

        lifecycleScope.launch(Dispatchers.IO) {
            if (categoryDao.getCount() == 0){


                val categories = listOf(
                    Category(name = "Salon", img = R.drawable.ic_salon),
                    Category(name = "Plumbing", img = R.drawable.ic_plumbing),
                    Category(name = "Planting", img = R.drawable.ic_planting),
                    Category(name = "Home Care", img = R.drawable.ic_home_care),
                    Category(name = "Entertainment", img = R.drawable.ic_entertainment),
                    Category(name = "Engine Repair", img = R.drawable.ic_engine_repair),
                    Category(name = "Electrician", img = R.drawable.ic_electrician),
                    Category(name = "Coaching", img = R.drawable.ic_coaching),
                    Category(name = "Chef", img = R.drawable.ic_chef),
                    Category(name = "Carpenter", img = R.drawable.ic_carpenter),
                    Category(name = "Car and Bike", img = R.drawable.ic_car_bike),
                    Category(name = "Beauty", img = R.drawable.ic_beauty)
                )
                categoryDao.insertCategories(categories)
            }


            // Insert Services
            val services = listOf(
                Service(
                    categoryId = 1,
                    name = "Haircut",
                    description = "Get a fresh haircut"),
                Service(
                    categoryId = 1,
                    name = "Shaving",
                    description = "Professional shaving service"
                ),
                Service(
                    categoryId = 1,
                    name = "kjdchd",
                    description = "Professional shaving service"
                ),
                Service(
                    categoryId = 1,
                    name = "dkjdk",
                    description = "Professional shaving service"
                ),
                Service(
                    categoryId = 1,
                    name = "manicure",
                    description = "Professional xyz service"
                ),
                Service(
                    categoryId = 1,
                    name = "Hairwash",
                    description = "Professional shaving service"
                ),
                Service(
                    categoryId = 1,
                    name = "pedicure",
                    description = "Professional shaving service"
                ),
                Service(
                    categoryId = 2,
                    name = "Shaving",
                    description = "Professional shaving service"
                ),
                Service(
                    categoryId = 1,
                    name = "Shaving",
                    description = "Professional shaving service"
                ),
                Service(
                    categoryId = 1,
                    name = "Shaving",
                    description = "Professional shaving service"
                ),
                Service(
                    categoryId = 2,
                    name = "Pipe Fixing",
                    description = "Expert plumbing services"
                ),
                Service(
                    categoryId = 2,
                    name = "Pipe dkjnd",
                    description = "Expert plumbing services"
                ),
                Service(
                    categoryId = 2,
                    name = "Pipe hkjb",
                    description = "Expert plumbing services"
                ),
                Service(
                    categoryId = 2,
                    name = "geaser issue",
                    description = "Expert plumbing services"
                ),
                Service(
                    categoryId = 2,
                    name = "Pipe Fixing",
                    description = "Expert plumbing services"
                ),
                Service(
                    categoryId = 2,
                    name = "Pipe Fixing",
                    description = "Expert plumbing services"
                ),
                Service(
                    categoryId = 2,
                    name = "Pipe Fixing",
                    description = "Expert plumbing services"
                ),
                Service(
                    categoryId = 2,
                    name = "Pipe Fixing",
                    description = "Expert plumbing services"
                ),
                Service(
                    categoryId = 2,
                    name = "Pipe Fixing",
                    description = "Expert plumbing services"
                ),
                Service(
                    categoryId = 2,
                    name = "Pipe Fixing",
                    description = "Expert plumbing services"
                ),
                Service(
                    categoryId = 2,
                    name = "Leak Repair",
                    description = "Leak repair in kitchen and bathroom"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "xyz",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree xjkd",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "soil",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Tree Planting",
                    description = "Plant trees at your home"
                ),
                Service(
                    categoryId = 3,
                    name = "Landscaping",
                    description = "Complete landscaping solutions"
                ),
                Service(
                    categoryId = 4,
                    name = "Cleaning",
                    description = "Home cleaning services"
                ),
                Service(
                    categoryId = 4,
                    name = "Gardening",
                    description = "Maintain your garden"
                ),
                Service(
                    categoryId = 5,
                    name = "DJ Services",
                    description = "Hiring DJs for events"
                ),
                Service(
                    categoryId = 5,
                    name = "Event Planning",
                    description = "Complete event planning solutions"
                ),
                Service(
                    categoryId = 5,
                    name = "Event Planning",
                    description = "Complete event planning solutions"
                ),
                Service(
                    categoryId = 5,
                    name = "Event Planning",
                    description = "Complete event planning solutions"
                ),
                Service(
                    categoryId = 5,
                    name = "Event Planning",
                    description = "Complete event planning solutions"
                )

                // Add more services as needed
            )
            serviceDao.insertServices(services)


            // Insert Service Providers

            val serviceProviders = listOf(
                ServiceProvider(
                    serviceId = 1,
                    name = "John boe",
                    imageResId = R.drawable.provider1,
                    rating = 4.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "1234567890"
                ),
                ServiceProvider(
                    serviceId = 1,
                    name = "John jb",
                    imageResId = R.drawable.provider2,
                    rating = 5.5f,
                    ratePerHour = 100.0,
                    phoneNumber = "1234567890"
                ),
                ServiceProvider(
                    serviceId = 1,
                    name = "John LMN",
                    imageResId = R.drawable.provider1,
                    rating = 4.2f,
                    ratePerHour = 19.0,
                    phoneNumber = "1234567890"
                ),
                ServiceProvider(
                    serviceId = 2,
                    name = "John toe",
                    imageResId = R.drawable.provider2,
                    rating = 3.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "123436470"
                ),
                ServiceProvider(
                    serviceId = 2,
                    name = "Aman",
                    imageResId = R.drawable.provider2,
                    rating = 3.9f,
                    ratePerHour = 20.8,
                    phoneNumber = "123436470"
                ),
                ServiceProvider(
                    serviceId = 3,
                    name = "John moe",
                    imageResId = R.drawable.provider1,
                    rating = 2.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "6789012345"

                ),
                ServiceProvider(
                    serviceId = 3,
                    name = "John moe",
                    imageResId = R.drawable.provider1,
                    rating = 2.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "678901445"

                ),
                ServiceProvider(
                    serviceId = 4,
                    name = "John JHGJ",
                    imageResId = R.drawable.provider2,
                    rating = 5.2f,
                    ratePerHour = 13.0,
                    phoneNumber = "6789012345"

                ),
                ServiceProvider(
                    serviceId = 4,
                    name = "John akkw",
                    imageResId = R.drawable.provider2,
                    rating = 1.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 5,
                    name = "John smnwms",
                    imageResId = R.drawable.provider1,
                    rating = 4.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 6,
                    name = "John sjw",
                    imageResId = R.drawable.provider2,
                    rating = 4.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 7,
                    name = "John oe",
                    imageResId = R.drawable.provider1,
                    rating = 4.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 8,
                    name = "John aa",
                    imageResId = R.drawable.provider2,
                    rating = 4.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 9,
                    name = "John lw",
                    imageResId = R.drawable.provider1,
                    rating = 4.5f,
                    ratePerHour = 17.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 10,
                    name = "John wl",
                    imageResId = R.drawable.provider2,
                    rating = 4.5f,
                    ratePerHour = 30.0,
                    phoneNumber = "78i74123456"


                ),
                ServiceProvider(
                    serviceId = 11,
                    name = "John lw",
                    imageResId = R.drawable.provider1,
                    rating = 4.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 12,
                    name = "John ke",
                    imageResId = R.drawable.provider2,
                    rating = 4.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 13,
                    name = "John fef",
                    imageResId = R.drawable.provider1,
                    rating = 4.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 14,
                    name = "John Doe",
                    imageResId = R.drawable.provider2,
                    rating = 4.5f,
                    ratePerHour = 20.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 15,
                    name = "Jane Smith",
                    imageResId = R.drawable.provider1,
                    rating = 4.2f,
                    ratePerHour = 18.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 16,
                    name = "Mike Johnson",
                    imageResId = R.drawable.provider2,
                    rating = 5.0f,
                    ratePerHour = 25.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 25,
                    name = "Jane ",
                    imageResId = R.drawable.provider1,
                    rating = 2.2f,
                    ratePerHour = 39.0,
                    phoneNumber = "7890123456"


                ),
                ServiceProvider(
                    serviceId = 45,
                    name = "Jane ",
                    imageResId = R.drawable.provider1,
                    rating = 3.2f,
                    ratePerHour = 48.0,
                    phoneNumber = "7890123456"


                ),
            )



            serviceProviderDao.insertServiceProviders(serviceProviders)
        }



    }
}

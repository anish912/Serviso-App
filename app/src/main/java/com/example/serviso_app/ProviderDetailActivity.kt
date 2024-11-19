package com.example.serviso_app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.Locale

class ProviderDetailActivity : AppCompatActivity() {



    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var provider: ServiceProvider
    private var bookingNotificationService: BookingNotificationService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as BookingNotificationService.BookingNotificationBinder
            bookingNotificationService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bookingNotificationService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_detail)

        val homebutton:FloatingActionButton=findViewById(R.id.homeButton)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)







        provider = intent.getSerializableExtra("provider") as ServiceProvider

        val providerImage: ImageView = findViewById(R.id.providerImage)
        val providerName: TextView = findViewById(R.id.providerName)
        val providerRatePerHour: TextView = findViewById(R.id.ratePerHour)
        val providerRatingText: TextView = findViewById(R.id.providerRatingText)


        providerName.text = provider.name
        providerRatePerHour.text = "Rate: $${provider.ratePerHour}/hr"
        providerRatingText.text = provider.rating.toString()
        providerImage.setImageResource(provider.imageResId)

        val callButton: FloatingActionButton = findViewById(R.id.callButton)
        callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${provider.phoneNumber}"))
            startActivity(intent)
        }
        callButton.setOnLongClickListener{
            Toast.makeText(this, "Call button long pressed", Toast.LENGTH_SHORT).show()
            true

        }



        // Book Now button
        val bookNowButton: Button = findViewById(R.id.bookNowButton)
        bookNowButton.setOnClickListener {
            showBookingDialog(homebutton)


        }

        // Message button
        val messageButton: FloatingActionButton = findViewById(R.id.messageButton)
        messageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${provider.phoneNumber}"))
            startActivity(intent)
        }
        messageButton.setOnLongClickListener{
            Toast.makeText(this, "Message button long pressed", Toast.LENGTH_SHORT).show()
            true

        }




    }

    private fun showBookingDialog(floatingButton:FloatingActionButton) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_booking, null)
        val builder = AlertDialog.Builder(this)
            .setTitle("Book Service")
            .setView(dialogView)
            .setCancelable(true)

        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
        val locationTextView = dialogView.findViewById<TextView>(R.id.locationTextView)
        val submitButton = dialogView.findViewById<Button>(R.id.submitButton)
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)





        getCurrentLocation(locationTextView)

        val dialog = builder.create()

        submitButton.setOnClickListener {
            val selectedDate = "${datePicker.dayOfMonth}-${datePicker.month + 1}-${datePicker.year}"
            val selectedTime = "${timePicker.hour}:${timePicker.minute}"
            val price = timePicker.hour*provider.ratePerHour

            val booking = Booking(
                serviceName = provider.name,
                providerName = provider.name,
                bookingDate = selectedDate,
                bookingTime = selectedTime,
                price = price,
                location = locationTextView.text.toString()
            )

            // Save booking in the database
            lifecycleScope.launch {
                (application as ServisoApp).database.bookingDao().insertBooking(booking)
            }
            Toast.makeText(this,"Yay have booked a service",Toast.LENGTH_SHORT).show()


            dialog.dismiss()

            bookingNotificationService?.sendNotification(booking)
            floatingButton.visibility = View.VISIBLE
            floatingButton.setOnClickListener{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }






        }

        dialog.show()
    }

    private fun getCurrentLocation(locationTextView: TextView) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    // Convert latitude and longitude to human-readable address using Geocoder
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address>? =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    if (addresses != null && addresses.isNotEmpty()) {
                        // Set the address in the TextView inside the dialog
                        locationTextView.text = "Address: ${addresses[0].getAddressLine(0)}"
                    } else {
                        Toast.makeText(this, "Unable to fetch address", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {
                Toast.makeText(this,"yay we got the location",Toast.LENGTH_LONG)
                    .show()
            }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                val locationTextView = findViewById<TextView>(R.id.locationTextView)
                getCurrentLocation(locationTextView)
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val intent = Intent(this, BookingNotificationService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }






}
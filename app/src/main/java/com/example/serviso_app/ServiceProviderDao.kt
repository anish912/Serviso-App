package com.example.serviso_app

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ServiceProviderDao {
    @Insert
    suspend fun insertServiceProvider(serviceProvider: ServiceProvider)

    @Insert
    suspend fun insertServiceProviders(serviceProviders: List<ServiceProvider>)

    @Query("SELECT * FROM service_providers WHERE serviceId = :serviceId ORDER BY rating DESC")
    fun getServiceProvidersByServiceId(serviceId: Int): LiveData<List<ServiceProvider>>


}

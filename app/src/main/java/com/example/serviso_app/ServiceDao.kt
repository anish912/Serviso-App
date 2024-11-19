package com.example.serviso_app

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ServiceDao {
    @Insert
    suspend fun insertService(service: Service)

    @Insert
    suspend fun insertServices(services: List<Service>)

    @Query("SELECT * FROM services WHERE name = :serviceName LIMIT 1")
    suspend fun getServiceByName(serviceName: String): Service


    @Query("SELECT * FROM services WHERE categoryId = :categoryId")
    fun getServicesByCategory(categoryId: Int): LiveData<List<Service>>
}
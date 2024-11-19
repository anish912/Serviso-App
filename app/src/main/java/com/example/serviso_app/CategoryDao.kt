package com.example.serviso_app

import androidx.annotation.DeprecatedSinceApi
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT COUNT(*) FROM categories")
    fun getCount(): Int



    @Insert
    suspend fun insertCategories(category: List<Category>)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<Category>>
}
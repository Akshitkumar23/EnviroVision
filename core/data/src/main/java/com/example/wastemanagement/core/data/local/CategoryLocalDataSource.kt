package com.example.wastemanagement.core.data.local

import com.example.wastemanagement.core.domain.entities.Category
import kotlinx.coroutines.flow.Flow

interface CategoryLocalDataSource {
    fun getCategories(): Flow<List<Category>>
    suspend fun saveCategories(categories: List<Category>)
}

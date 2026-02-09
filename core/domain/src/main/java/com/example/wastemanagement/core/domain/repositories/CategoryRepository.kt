package com.example.wastemanagement.core.domain.repositories

import com.example.wastemanagement.core.domain.entities.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
}
package com.example.wastemanagement.core.data.remote

import com.example.wastemanagement.core.domain.entities.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRemoteDataSource {
    fun getCategories(): Flow<List<Category>>
}

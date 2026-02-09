package com.example.wastemanagement.core.network.api

import com.example.wastemanagement.core.network.dto.CategoryDto
import retrofit2.http.GET

interface CategoryApi {
    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>
}

package com.example.wastemanagement.core.network

import com.example.wastemanagement.core.data.remote.CategoryRemoteDataSource
import com.example.wastemanagement.core.domain.entities.Category
import com.example.wastemanagement.core.network.api.CategoryApi
import com.example.wastemanagement.core.network.mappers.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CategoryRemoteDataSourceImpl(
    private val categoryApi: CategoryApi
) : CategoryRemoteDataSource {

    override fun getCategories(): Flow<List<Category>> = flow {
        emit(categoryApi.getCategories().map { it.toDomain() })
    }
}
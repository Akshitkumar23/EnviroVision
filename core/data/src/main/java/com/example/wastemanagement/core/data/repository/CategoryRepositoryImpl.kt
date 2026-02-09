package com.example.wastemanagement.core.data.repository

import com.example.wastemanagement.core.data.local.CategoryLocalDataSource
import com.example.wastemanagement.core.data.remote.CategoryRemoteDataSource
import com.example.wastemanagement.core.domain.entities.Category
import com.example.wastemanagement.core.domain.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CategoryRepositoryImpl(
    private val localDataSource: CategoryLocalDataSource,
    private val remoteDataSource: CategoryRemoteDataSource
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> = flow {
        try {
            val remoteCategories = remoteDataSource.getCategories().first()
            localDataSource.saveCategories(remoteCategories)
            emit(remoteCategories)
        } catch (e: Exception) {
            emit(localDataSource.getCategories().first())
        }
    }
}
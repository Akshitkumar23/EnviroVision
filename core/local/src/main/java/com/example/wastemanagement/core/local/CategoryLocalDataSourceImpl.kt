package com.example.wastemanagement.core.local

import com.example.wastemanagement.core.data.local.CategoryLocalDataSource
import com.example.wastemanagement.core.domain.entities.Category
import com.example.wastemanagement.core.local.dao.CategoryDao
import com.example.wastemanagement.core.local.mappers.toDomain
import com.example.wastemanagement.core.local.mappers.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryLocalDataSourceImpl(
    private val categoryDao: CategoryDao
) : CategoryLocalDataSource {

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getCategories().map { it.map { category -> category.toDomain() } }
    }

    override suspend fun saveCategories(categories: List<Category>) {
        categoryDao.insertCategories(categories.map { it.toEntity() })
    }
}

package com.example.wastemanagement.core.domain.usecases

import com.example.wastemanagement.core.domain.entities.Category
import com.example.wastemanagement.core.domain.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(private val categoryRepository: CategoryRepository) {
    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.getCategories()
    }
}

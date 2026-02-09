package com.example.wastemanagement.core.domain.repositories

import com.example.wastemanagement.core.domain.entities.User

interface UserRepository {
    suspend fun getUser(userId: String): User?
    suspend fun saveUser(user: User)
}

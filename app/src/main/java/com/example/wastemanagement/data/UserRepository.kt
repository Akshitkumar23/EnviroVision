package com.example.wastemanagement.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    // This is a placeholder for a real user management system.
    // In a real app, this would come from a database, DataStore, or network call.
    private var currentUserRole: String = "admin" // Defaulting to admin for now

    suspend fun getUserRole(): String {
        return currentUserRole
    }

}

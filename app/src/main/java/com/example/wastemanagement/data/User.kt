package com.example.wastemanagement.data

import com.google.firebase.firestore.PropertyName

// Firestore document model for a user
data class User(
    val uid: String = "",
    val displayName: String? = null,
    val email: String? = null,
    val role: String = "citizen",
    @get:PropertyName("isDisabled") // Ensures Firestore uses this name
    val isDisabled: Boolean = false
)

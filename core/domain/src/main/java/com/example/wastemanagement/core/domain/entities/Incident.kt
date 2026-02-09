package com.example.wastemanagement.core.domain.entities

import java.util.Date

data class Incident(
    val id: String,
    val categoryId: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String?,
    val status: String, // e.g., "Reported", "In Progress", "Resolved"
    val reportedAt: Date,
    val reportedBy: String // User ID
)

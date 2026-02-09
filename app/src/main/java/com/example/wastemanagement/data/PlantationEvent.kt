package com.example.wastemanagement.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PlantationEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val locationLatLng: String = "0.0,0.0", // Storing LatLng as a string "lat,lng"
    @ServerTimestamp
    val eventDate: Date? = null,
    val requiredVolunteers: Int = 0,
    val registeredVolunteers: List<String> = emptyList(), // List of User UIDs
    val createdBy: String = "", // Admin UID
    @ServerTimestamp
    val createdAt: Date? = null
)

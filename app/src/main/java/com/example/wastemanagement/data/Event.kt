package com.example.wastemanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "events")
data class Event(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val date: Long = 0L, // Storing date as a timestamp for Room
    val createdBy: String = "", // Admin ID
    val registrationLimit: Int = 0,
    val createdAt: Date? = null, // Using Date, which will be handled by the TypeConverter
    val participants: List<String> = emptyList() // Will be handled by TypeConverter
)

// This is just a data class, not a database entity, so it doesn't need annotations.
data class EventParticipation(
    val eventId: String = "",
    val userId: String = "",
    val userName: String = "",
    val timestamp: Long = 0L,
    val photoProofUri: String? = null
)

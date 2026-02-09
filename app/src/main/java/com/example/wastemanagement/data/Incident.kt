package com.example.wastemanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class Incident(
    @PrimaryKey
    val id: String = "",
    val type: String = "",
    val description: String = "",
    val location: String = "", // Lat,Lng as a string
    val imageUris: List<String> = emptyList(),
    val status: String = "Reported", // e.g., Reported, In Progress, Resolved, Rejected, Merged
    val severity: String = "Medium", // e.g., Low, Medium, High
    val timestamp: Long = System.currentTimeMillis(),
    val reportedBy: String = "", // User ID of the citizen who reported it
    val date: String = "",
    val afterImageUri: String? = null, // URI of the photo uploaded by admin after resolution
    val resolvedComment: String? = null, // Optional comment from the admin on resolution
    val assignedTo: String? = null, // User ID of the admin it is assigned to
    val masterIncidentId: String? = null, // If this is a merged incident, this points to the master
    val mergedIds: List<String> = emptyList() // If this is a master, this lists the merged incidents
)

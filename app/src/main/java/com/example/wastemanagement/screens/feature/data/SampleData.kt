package com.example.wastemanagement.screens.feature.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

// A more complete data class for an incident
data class Incident(
    val id: String,
    val type: String,
    val description: String,
    val date: String,
    var status: String,
    val severity: String,
    val reportedBy: String,
    val location: String, // Could be a LatLng object in a real app
    val images: List<String>, // List of image URLs
    val icon: ImageVector // For UI representation
)

object SampleData {
    val allIncidents = listOf(
        Incident(
            id = "1",
            type = "Solid Waste",
            description = "Overflowing bin on Main St",
            date = "2024-05-20",
            status = "Open",
            severity = "High",
            reportedBy = "User123",
            location = "Main St & 1st Ave",
            images = emptyList(),
            icon = Icons.Default.Delete
        ),
        Incident(
            id = "2",
            type = "Water",
            description = "Illegal dumping near park",
            date = "2024-05-19",
            status = "In Progress",
            severity = "Medium",
            reportedBy = "User456",
            location = "Riverside Park",
            images = emptyList(),
            icon = Icons.Default.WaterDrop
        ),
        Incident(
            id = "3",
            type = "Hazardous",
            description = "Chemical spill behind factory",
            date = "2024-05-18",
            status = "Resolved",
            severity = "High",
            reportedBy = "Admin",
            location = "Industrial Zone B",
            images = emptyList(),
            icon = Icons.Default.Warning
        )
    )
}

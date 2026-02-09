package com.example.wastemanagement.core.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val incidentType: String,
    val subTypes: List<String>,
    val severityLevels: List<String>,
    val notificationRules: String // Store as JSON string
)
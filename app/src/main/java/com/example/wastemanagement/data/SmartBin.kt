package com.example.wastemanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smart_bins")
data class SmartBin(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val fillLevel: Int = 0, // Percentage from 0 to 100
    val lastUpdated: Long = 0L
)

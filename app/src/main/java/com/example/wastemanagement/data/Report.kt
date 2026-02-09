package com.example.wastemanagement.data

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Report(
    var id: String = "",
    val userId: String = "",
    val type: String = "",
    val description: String = "",
    val location: GeoPoint? = null,
    val imageUrls: List<String> = emptyList(),
    var status: String = "Reported",
    val severity: String = "",
    @ServerTimestamp
    val createdAt: Date? = null
)

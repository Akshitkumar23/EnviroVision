package com.example.wastemanagement.components

import androidx.compose.ui.graphics.Color

// Colors for Incident Status
fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "pending" -> Color(0xFFFFA726) // Orange
        "in progress" -> Color(0xFF29B6F6) // Blue
        "resolved" -> Color(0xFF66BB6A) // Green
        "rejected" -> Color(0xFFEF5350) // Red
        else -> Color.Gray
    }
}

// Colors for Incident Severity
fun getSeverityColor(severity: String): Color {
    return when (severity.lowercase()) {
        "low" -> Color(0xFF66BB6A) // Green
        "medium" -> Color(0xFFFFCA28) // Amber
        "high" -> Color(0xFFEF5350) // Red
        else -> Color.Gray
    }
}

// Colors for Incident Type (for Charts)
fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "illegal dumping" -> Color(0xFFAB47BC) // Purple
        "overflowing bin" -> Color(0xFF5C6BC0) // Indigo
        "damaged bin" -> Color(0xFF78909C) // Blue Grey
        "other" -> Color(0xFF8D6E63) // Brown
        else -> Color.LightGray
    }
}

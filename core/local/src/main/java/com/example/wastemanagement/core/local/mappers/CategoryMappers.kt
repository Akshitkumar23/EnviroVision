package com.example.wastemanagement.core.local.mappers

import com.example.wastemanagement.core.domain.entities.Category
import com.example.wastemanagement.core.domain.entities.NotificationRules
import com.example.wastemanagement.core.domain.entities.Severity
import com.example.wastemanagement.core.local.models.CategoryEntity
import com.google.gson.Gson

fun CategoryEntity.toDomain(): Category {
    val notificationRules = Gson().fromJson(notificationRules, NotificationRules::class.java)
    return Category(
        id = id,
        incidentType = incidentType,
        subTypes = subTypes,
        severityLevels = severityLevels.map { 
            when(it) {
                "Low" -> Severity.Low
                "Medium" -> Severity.Medium
                "High" -> Severity.High
                else -> throw IllegalArgumentException("Unknown severity level: $it")
            }
        },
        notificationRules = notificationRules
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        incidentType = incidentType,
        subTypes = subTypes,
        severityLevels = severityLevels.map { 
            when(it) {
                is Severity.Low -> "Low"
                is Severity.Medium -> "Medium"
                is Severity.High -> "High"
            }
        },
        notificationRules = Gson().toJson(notificationRules)
    )
}

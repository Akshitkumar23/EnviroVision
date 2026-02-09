package com.example.wastemanagement.core.network.mappers

import com.example.wastemanagement.core.domain.entities.Category
import com.example.wastemanagement.core.domain.entities.NotificationRules
import com.example.wastemanagement.core.domain.entities.Severity
import com.example.wastemanagement.core.network.dto.CategoryDto

fun CategoryDto.toDomain(): Category {
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
        notificationRules = NotificationRules(
            channels = notificationRules.channels,
            thresholds = notificationRules.thresholds
        )
    )
}

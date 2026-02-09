package com.example.wastemanagement.core.network.dto

data class CategoryDto(
    val id: String,
    val incidentType: String,
    val subTypes: List<String>,
    val severityLevels: List<String>,
    val notificationRules: NotificationRulesDto
)

data class NotificationRulesDto(
    val channels: List<String>,
    val thresholds: Map<String, String>
)
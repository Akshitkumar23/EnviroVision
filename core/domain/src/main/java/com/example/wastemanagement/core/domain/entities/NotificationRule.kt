package com.example.wastemanagement.core.domain.entities

data class NotificationRule(
    val id: String,
    val appliesToType: String,
    val severity: Severity,
    val channels: List<String>,
    val templateIds: List<String>
)
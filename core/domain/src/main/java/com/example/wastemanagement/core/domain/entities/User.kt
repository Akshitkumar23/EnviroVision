package com.example.wastemanagement.core.domain.entities

data class User(
    val id: String,
    val phoneNumber: String,
    val name: String?,
    val email: String?
)

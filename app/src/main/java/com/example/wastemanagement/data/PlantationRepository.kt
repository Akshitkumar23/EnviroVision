package com.example.wastemanagement.data

import kotlinx.coroutines.flow.Flow

interface PlantationRepository {
    fun getAllDrives(): Flow<List<PlantationEvent>>
    suspend fun addDrive(event: PlantationEvent)
    suspend fun rsvpToDrive(eventId: String, userId: String)
}

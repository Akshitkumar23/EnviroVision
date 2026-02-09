package com.example.wastemanagement.data

import com.example.wastemanagement.data.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getUpcomingEvents(): Flow<List<Event>>
    fun getCompletedEvents(): Flow<List<Event>>
    suspend fun createEvent(event: Event): Result<Unit>
    suspend fun rsvpToEvent(eventId: String, userId: String): Result<Unit>
    suspend fun uploadParticipationProof(eventId: String, userId: String, photoUri: String): Result<Unit>
}

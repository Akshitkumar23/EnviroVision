package com.example.wastemanagement.data

import kotlinx.coroutines.flow.Flow

interface IncidentRepository {
    fun getAllIncidents(): Flow<List<Incident>>
    fun getIncidentsForUser(userId: String): Flow<List<Incident>> // Added this method
    fun getIncidentById(id: String): Flow<Incident?>
    suspend fun insertIncident(incident: Incident)
    suspend fun updateIncident(incident: Incident)
    suspend fun deleteIncident(incident: Incident)
    suspend fun deleteIncidentById(id: String)
}

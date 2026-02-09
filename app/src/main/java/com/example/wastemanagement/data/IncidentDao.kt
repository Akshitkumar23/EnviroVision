package com.example.wastemanagement.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: Incident)

    @Query("SELECT * FROM incidents")
    fun getAllIncidents(): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE reportedBy = :userId")
    fun getIncidentsForUser(userId: String): Flow<List<Incident>>

    @Query("SELECT * FROM incidents WHERE id = :id")
    fun getIncidentById(id: String): Flow<Incident?>

    @Delete
    suspend fun deleteIncident(incident: Incident)

    @Query("DELETE FROM incidents WHERE id = :id")
    suspend fun deleteIncidentById(id: String)

    @Query("DELETE FROM incidents")
    suspend fun deleteAllIncidents()
}

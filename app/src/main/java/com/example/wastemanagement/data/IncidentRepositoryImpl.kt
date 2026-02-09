package com.example.wastemanagement.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class IncidentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val incidentDao: IncidentDao
) : IncidentRepository {

    override fun getAllIncidents(): Flow<List<Incident>> = callbackFlow {
        val listener = firestore.collection("incidents")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val incidents = snapshot.toObjects(Incident::class.java)
                    trySend(incidents)
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getIncidentsForUser(userId: String): Flow<List<Incident>> = callbackFlow {
        val listener = firestore.collection("incidents")
            .whereEqualTo("reportedBy", userId) // Temporarily removing orderBy for debugging
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val incidents = snapshot.toObjects(Incident::class.java)
                    trySend(incidents)
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getIncidentById(id: String): Flow<Incident?> = callbackFlow {
        val docRef = firestore.collection("incidents").document(id)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(Incident::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun insertIncident(incident: Incident) {
        incidentDao.insertIncident(incident)
        firestore.collection("incidents").document(incident.id).set(incident).await()
    }

    override suspend fun updateIncident(incident: Incident) {
        incidentDao.insertIncident(incident)
        firestore.collection("incidents").document(incident.id).set(incident).await()
    }

    override suspend fun deleteIncident(incident: Incident) {
        incidentDao.deleteIncident(incident)
        firestore.collection("incidents").document(incident.id).delete().await()
    }

    override suspend fun deleteIncidentById(id: String) {
        incidentDao.deleteIncidentById(id)
        firestore.collection("incidents").document(id).delete().await()
    }
}

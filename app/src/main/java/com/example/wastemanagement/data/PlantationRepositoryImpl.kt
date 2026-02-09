package com.example.wastemanagement.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlantationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PlantationRepository {

    override fun getAllDrives(): Flow<List<PlantationEvent>> = callbackFlow {
        val subscription = firestore.collection("plantation_drives")
            .orderBy("eventDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val events = snapshot.toObjects(PlantationEvent::class.java)
                    trySend(events).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun addDrive(event: PlantationEvent) {
        firestore.collection("plantation_drives").document(event.id).set(event).await()
    }

    override suspend fun rsvpToDrive(eventId: String, userId: String) {
        val driveRef = firestore.collection("plantation_drives").document(eventId)
        driveRef.update("registeredVolunteers", FieldValue.arrayUnion(userId)).await()
    }
}

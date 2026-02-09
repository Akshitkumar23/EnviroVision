package com.example.wastemanagement.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : EventRepository {

    private val eventsCollection = firestore.collection("events")

    override fun getUpcomingEvents(): Flow<List<Event>> = callbackFlow {
        val listener = eventsCollection
            .whereGreaterThan("date", System.currentTimeMillis())
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("EventRepository", "Error fetching upcoming events", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val events = snapshot.toObjects(Event::class.java)
                    trySend(events).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getCompletedEvents(): Flow<List<Event>> = callbackFlow {
        val listener = eventsCollection
            .whereLessThanOrEqualTo("date", System.currentTimeMillis())
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("EventRepository", "Error fetching completed events", error)
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val events = snapshot.toObjects(Event::class.java)
                    trySend(events).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createEvent(event: Event): Result<Unit> = try {
        eventsCollection.document(event.id).set(event).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("EventRepository", "Error creating event", e)
        Result.failure(e)
    }

    override suspend fun rsvpToEvent(eventId: String, userId: String): Result<Unit> = try {
        eventsCollection.document(eventId).update("participants", FieldValue.arrayUnion(userId)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("EventRepository", "Error RSVPing to event", e)
        Result.failure(e)
    }

    override suspend fun uploadParticipationProof(eventId: String, userId: String, photoUri: String): Result<Unit> {
        // This will require Firebase Storage. We will implement this in a later step.
        // For now, we can add the logic to update a Firestore document with the photo URL.
        return Result.success(Unit) // Placeholder
    }
}

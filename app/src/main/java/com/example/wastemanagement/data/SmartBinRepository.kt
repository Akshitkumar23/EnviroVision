package com.example.wastemanagement.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartBinRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    fun getSmartBins(): Flow<List<SmartBin>> = callbackFlow {
        val collection = firestore.collection("smart_bins")

        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val smartBins = snapshot.toObjects(SmartBin::class.java)
                trySend(smartBins)
            }
        }

        awaitClose { listener.remove() }
    }
}

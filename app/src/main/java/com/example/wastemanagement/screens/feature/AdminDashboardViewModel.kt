package com.example.wastemanagement.screens.feature

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.Incident
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _dashboardStats = MutableStateFlow<DashboardStats?>(null)
    val dashboardStats = _dashboardStats.asStateFlow()

    private val _allIncidents = MutableStateFlow<List<Incident>>(emptyList())

    val searchQuery = MutableStateFlow("")
    val statusFilter = MutableStateFlow("All")
    val sortBy = MutableStateFlow("Newest")
    val dateRange = MutableStateFlow<Pair<Long, Long>?>(null)

    val filteredIncidents: StateFlow<List<Incident>> = combine(
        _allIncidents, searchQuery, statusFilter, sortBy, dateRange
    ) { incidents, query, status, sort, date ->
        val filteredByStatus = when (status) {
            "Pending" -> incidents.filter { it.status.equals("Reported", ignoreCase = true) || it.status.equals("In Progress", ignoreCase = true) }
            "Resolved" -> incidents.filter { it.status.equals("Resolved", ignoreCase = true) }
            else -> incidents
        }

        val filteredByDate = if (date != null) {
            filteredByStatus.filter { it.timestamp >= date.first && it.timestamp <= date.second }
        } else {
            filteredByStatus
        }

        val searchedList = if (query.isBlank()) {
            filteredByDate
        } else {
            filteredByDate.filter {
                it.type.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.id.contains(query, ignoreCase = true) ||
                it.reportedBy.contains(query, ignoreCase = true)
            }
        }

        when (sort) {
            "Oldest" -> searchedList.sortedBy { it.timestamp }
            "Severity" -> searchedList.sortedByDescending { 
                when(it.severity.lowercase()) {
                    "high" -> 3
                    "medium" -> 2
                    "low" -> 1
                    else -> 0
                }
            }
            else -> searchedList
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchDashboardData()
    }

    fun onSearchQueryChanged(query: String) { searchQuery.value = query }
    fun onStatusFilterChanged(status: String) { statusFilter.value = status }
    fun onSortChanged(sortOption: String) { sortBy.value = sortOption }
    fun onDateRangeSelected(range: Pair<Long, Long>?) { dateRange.value = range }

    fun updateIncidentStatus(incidentId: String, newStatus: String, afterImageUri: Uri? = null, comment: String? = null) {
        viewModelScope.launch {
            try {
                val incidentRef = firestore.collection("incidents").document(incidentId)
                
                if (newStatus == "Resolved") {
                    if (afterImageUri == null) {
                        Log.e("UpdateStatus", "After image is required to resolve an incident.")
                        return@launch
                    }
                    val imageUrl = storage.reference.child("after_images/${UUID.randomUUID()}")
                        .putFile(afterImageUri)
                        .await()
                        .storage
                        .downloadUrl
                        .await()
                        .toString()
                    
                    incidentRef.update(
                        mapOf(
                            "status" to newStatus,
                            "afterImageUri" to imageUrl,
                            "resolvedComment" to comment
                        )
                    ).await()

                } else {
                    incidentRef.update("status", newStatus).await()
                }
                fetchDashboardData()
            } catch (e: Exception) {
                Log.e("UpdateStatus", "Error updating incident status", e)
            }
        }
    }

    fun fetchDashboardData() {
        viewModelScope.launch {
            try {
                val incidentsSnapshot = firestore.collection("incidents")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                val incidents = incidentsSnapshot.toObjects(Incident::class.java)
                _allIncidents.value = incidents
                
                val usersSnapshot = firestore.collection("users").get().await()
                _dashboardStats.value = DashboardStats(
                    totalIncidents = incidents.size,
                    pendingIncidents = incidents.count { it.status.equals("Reported", ignoreCase = true) || it.status.equals("In Progress", ignoreCase = true) },
                    resolvedIncidents = incidents.count { it.status.equals("Resolved", ignoreCase = true) },
                    totalUsers = usersSnapshot.size()
                )

            } catch (e: Exception) {
                Log.e("AdminDashboardVM", "Error fetching dashboard data", e)
            }
        }
    }
}

data class DashboardStats(
    val totalIncidents: Int = 0,
    val pendingIncidents: Int = 0,
    val resolvedIncidents: Int = 0,
    val totalUsers: Int = 0
)

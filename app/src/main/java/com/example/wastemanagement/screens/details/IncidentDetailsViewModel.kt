package com.example.wastemanagement.screens.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.data.IncidentRepository
import com.example.wastemanagement.data.User
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class IncidentDetailsViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val firestore: FirebaseFirestore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _incident = MutableStateFlow<Incident?>(null)
    val incident = _incident.asStateFlow()

    private val _reporter = MutableStateFlow<User?>(null)
    val reporter = _reporter.asStateFlow()

    // Channel for one-time events like navigation
    private val _navigationChannel = Channel<Boolean>()
    val navigateBack = _navigationChannel.receiveAsFlow()

    init {
        savedStateHandle.get<String>("incidentId")?.let {
            if (it.isNotBlank()) { // Ensure incidentId is not blank
                loadIncident(it)
            }
        }
    }

    private fun loadIncident(id: String) {
        viewModelScope.launch {
            incidentRepository.getIncidentById(id).collect { incident ->
                _incident.value = incident
                incident?.reportedBy?.let { userId ->
                    if (userId.isNotBlank()) { // Ensure userId is not blank
                        fetchReporterDetails(userId)
                    }
                }
            }
        }
    }

    private fun fetchReporterDetails(userId: String) {
        viewModelScope.launch {
            try {
                val userDoc = firestore.collection("users").document(userId).get().await()
                _reporter.value = userDoc.toObject(User::class.java)
            } catch (e: Exception) {
                // Handle error, maybe set a default user or error state
            }
        }
    }

    fun deleteIncident() {
        viewModelScope.launch {
            _incident.value?.let {
                incidentRepository.deleteIncident(it)
                // Send navigation event after deletion
                _navigationChannel.send(true)
            }
        }
    }
}

package com.example.wastemanagement.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.data.IncidentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: IncidentRepository
) : ViewModel() {

    val incidents: StateFlow<List<Incident>> = repository.getAllIncidents()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateIncidentStatus(incident: Incident, newStatus: String) {
        viewModelScope.launch {
            val updatedIncident = incident.copy(status = newStatus)
            repository.updateIncident(updatedIncident)
        }
    }

    fun addAfterImage(incident: Incident, afterImageUri: String) {
        viewModelScope.launch {
            val updatedIncident = incident.copy(afterImageUri = afterImageUri)
            repository.updateIncident(updatedIncident)
        }
    }

    // We can add functions for assigning incidents, changing priority, etc., here later.
}

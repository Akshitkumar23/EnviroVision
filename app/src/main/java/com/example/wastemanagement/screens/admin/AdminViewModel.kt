package com.example.wastemanagement.screens.admin

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.data.IncidentRepository
import com.example.wastemanagement.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _incidents = MutableStateFlow<List<Incident>>(emptyList())
    val incidents = _incidents.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole = _userRole.asStateFlow()

    val showStatusDialog = mutableStateOf<Incident?>(null)
    val showAfterPhotoDialog = mutableStateOf<Incident?>(null)

    init {
        loadIncidents()
        loadUserRole()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            _userRole.value = userRepository.getUserRole()
        }
    }

    fun loadIncidents() {
        viewModelScope.launch {
            incidentRepository.getAllIncidents().collect {
                _incidents.value = it
            }
        }
    }

    fun updateIncidentStatus(incident: Incident, newStatus: String) {
        viewModelScope.launch {
            val updatedIncident = incident.copy(status = newStatus)
            incidentRepository.updateIncident(updatedIncident)
        }
    }

    fun uploadAfterPhoto(incident: Incident, photoUri: Uri) {
        viewModelScope.launch {
            val updatedIncident = incident.copy(afterImageUri = photoUri.toString())
            incidentRepository.updateIncident(updatedIncident)
        }
    }
}

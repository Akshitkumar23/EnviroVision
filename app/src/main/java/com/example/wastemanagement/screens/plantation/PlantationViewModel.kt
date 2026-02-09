package com.example.wastemanagement.screens.plantation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.PlantationEvent
import com.example.wastemanagement.data.PlantationRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PlantationViewModel @Inject constructor(
    private val repository: PlantationRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _allDrives: StateFlow<List<PlantationEvent>> = repository.getAllDrives()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingDrives: StateFlow<List<PlantationEvent>> = _allDrives.map {
        it.filter { drive -> drive.eventDate?.after(Date()) ?: false }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pastDrives: StateFlow<List<PlantationEvent>> = _allDrives.map {
        it.filter { drive -> drive.eventDate?.before(Date()) ?: true }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createDrive(title: String, description: String, location: String, requiredVolunteers: Int, eventDate: Date) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val newEvent = PlantationEvent(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                location = location,
                eventDate = eventDate,
                requiredVolunteers = requiredVolunteers,
                createdBy = userId
            )
            repository.addDrive(newEvent)
        }
    }

    fun rsvpToDrive(eventId: String) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            repository.rsvpToDrive(eventId, userId)
        }
    }
}

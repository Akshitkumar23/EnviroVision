package com.example.wastemanagement.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.Event
import com.example.wastemanagement.data.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    val upcomingEvents: StateFlow<List<Event>> = eventRepository.getUpcomingEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedEvents: StateFlow<List<Event>> = eventRepository.getCompletedEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.createEvent(event)
        }
    }

    fun rsvpToEvent(eventId: String, userId: String) {
        viewModelScope.launch {
            eventRepository.rsvpToEvent(eventId, userId)
        }
    }
}

package com.example.wastemanagement.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.data.IncidentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: IncidentRepository
) : ViewModel() {

    val allIncidents: StateFlow<List<Incident>> = repository.getAllIncidents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // State for the currently selected incident type filter
    private val _selectedIncidentType = MutableStateFlow("All")
    val selectedIncidentType: StateFlow<String> = _selectedIncidentType

    // Incidents filtered by the selected type
    val filteredIncidents: StateFlow<List<Incident>> = 
        combine(allIncidents, _selectedIncidentType) { incidents, type ->
            if (type == "All") {
                incidents
            } else {
                incidents.filter { it.type.equals(type, ignoreCase = true) }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Incidents grouped by type for a Pie Chart
    val incidentsByType: StateFlow<Map<String, Int>> = allIncidents.map {
        it.groupBy { incident -> incident.type }
            .mapValues { entry -> entry.value.size }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Incidents grouped by status for a Bar Chart
    val incidentsByStatus: StateFlow<Map<String, Int>> = allIncidents.map {
        it.groupBy { incident -> incident.status }
            .mapValues { entry -> entry.value.size }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Incidents grouped by severity for another Bar Chart
    val incidentsBySeverity: StateFlow<Map<String, Int>> = allIncidents.map {
        it.groupBy { incident -> incident.severity }
            .mapValues { entry -> entry.value.size }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Weekly incidents for the trend chart
    val weeklyIncidents: StateFlow<Map<String, Int>> = allIncidents.map {
        val today = LocalDate.now()
        val sevenDaysAgo = today.minusDays(7)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        try {
            it.filter { incident ->
                val incidentDate = LocalDate.parse(incident.date, formatter)
                !incidentDate.isBefore(sevenDaysAgo) && !incidentDate.isAfter(today)
            }
            .groupBy { incident -> incident.date } // Group by date
            .mapValues { entry -> entry.value.size } // Count incidents per day
        } catch (e: Exception) {
            emptyMap<String, Int>()
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun onIncidentTypeSelected(type: String) {
        _selectedIncidentType.value = type
    }
}
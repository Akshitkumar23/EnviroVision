package com.example.wastemanagement.screens.myreports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.data.IncidentRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MyReportsViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _incidents = MutableStateFlow<List<Incident>>(emptyList())

    private val _selectedStatus = MutableStateFlow("All")
    val selectedStatus = _selectedStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val filteredIncidents = _incidents.combine(selectedStatus) { allIncidents, status ->
        if (status == "All") {
            allIncidents
        } else {
            allIncidents.filter { it.status == status }
        }
    }

    val groupedAndFilteredIncidents = filteredIncidents.combine(selectedStatus) { incidents, _ ->
        withContext(Dispatchers.Default) {
            incidents.groupBy { getRelativeDate(it.date) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())


    init {
        loadUserIncidents()
    }

    fun onFilterSelected(status: String) {
        _selectedStatus.value = status
    }

    fun deleteIncident(incident: Incident) {
        viewModelScope.launch {
            incidentRepository.deleteIncident(incident)
        }
    }

    fun undoDelete(incident: Incident) {
        viewModelScope.launch {
            incidentRepository.insertIncident(incident)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            // Since we are using a snapshot listener, the data is already live.
            // The refresh is mainly for user feedback. We simulate a network delay.
            delay(1500)
            _isRefreshing.value = false
        }
    }

    private fun getRelativeDate(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = try {
            sdf.parse(dateString)
        } catch (e: Exception) {
            return dateString
        }
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)

        calendar.time = date ?: return dateString
        val incidentDay = calendar.get(Calendar.DAY_OF_YEAR)
        val incidentYear = calendar.get(Calendar.YEAR)

        return when {
            year == incidentYear && today == incidentDay -> "Today"
            year == incidentYear && today - 1 == incidentDay -> "Yesterday"
            else -> SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
        }
    }

    private fun loadUserIncidents() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                incidentRepository.getIncidentsForUser(userId).collect {
                    _incidents.value = it
                }
            }
        }
    }
}
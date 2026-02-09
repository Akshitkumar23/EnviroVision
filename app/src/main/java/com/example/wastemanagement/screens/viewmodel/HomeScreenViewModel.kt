package com.example.wastemanagement.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.data.IncidentRepository
import com.example.wastemanagement.data.SmartBin
import com.example.wastemanagement.data.SmartBinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val smartBinRepository: SmartBinRepository
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _incidents = incidentRepository.getAllIncidents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _smartBins = smartBinRepository.getSmartBins()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncidents: StateFlow<Int> = _incidents.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val openIssues: StateFlow<Int> = _incidents.map { it.count { incident -> incident.status == "Reported" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val resolvedIncidents: StateFlow<Int> = _incidents.map { it.count { incident -> incident.status == "Resolved" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalBins: StateFlow<Int> = _smartBins.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _selectedCategory = MutableStateFlow<String?>("All")
    val selectedCategory: StateFlow<String?> = _selectedCategory

    val filteredIncidents: StateFlow<List<Incident>> = combine(_incidents, _selectedCategory) { incidents, category ->
        if (category == "All" || category == null) {
            incidents
        } else {
            incidents.filter { it.type.equals(category, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchResults: StateFlow<Pair<List<Incident>, List<SmartBin>>> = 
        combine(searchText, _incidents, _smartBins) { text, incidents, bins ->
            if (text.isBlank()) {
                Pair(emptyList(), emptyList())
            } else {
                val filteredIncidents = incidents.filter {
                    it.description.contains(text, ignoreCase = true) || 
                    it.type.contains(text, ignoreCase = true)
                }
                val filteredBins = bins.filter {
                    it.name.contains(text, ignoreCase = true)
                }
                Pair(filteredIncidents, filteredBins)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(emptyList(), emptyList()))

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }
}

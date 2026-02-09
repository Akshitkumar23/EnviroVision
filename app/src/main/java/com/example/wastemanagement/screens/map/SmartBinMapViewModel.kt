package com.example.wastemanagement.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.SmartBin
import com.example.wastemanagement.data.SmartBinRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SmartBinMapViewModel @Inject constructor(
    private val repository: SmartBinRepository
) : ViewModel() {

    val bins: StateFlow<List<SmartBin>> = repository.getSmartBins()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // You can add state for the current camera position, filters, etc. here later.
    val delhiLatLng = LatLng(28.6139, 77.2090)
}

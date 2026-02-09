package com.example.wastemanagement.screens.report

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class ReportIncidentUiState(
    val incidentType: String = "",
    val customIncidentType: String = "",
    val description: String = "",
    val severity: String = "Medium",
    val location: LatLng = LatLng(28.6139, 77.2090), // Default to Delhi
    val imageUris: List<Uri> = emptyList(),
    val isEditing: Boolean = false,
    val suggestedCategory: String? = null,
    val isLoading: Boolean = false
)

sealed class ReportIncidentUiEvent {
    data class OnIncidentTypeChange(val incidentType: String) : ReportIncidentUiEvent()
    data class OnCustomIncidentTypeChange(val customIncidentType: String) : ReportIncidentUiEvent()
    data class OnDescriptionChange(val description: String) : ReportIncidentUiEvent()
    data class OnSeverityChange(val severity: String) : ReportIncidentUiEvent()
    data class OnLocationUpdate(val location: LatLng) : ReportIncidentUiEvent()
    data class OnImagesSelected(val uris: List<Uri>) : ReportIncidentUiEvent()
    data object OnSuggestionClicked : ReportIncidentUiEvent()
    data class SubmitOrUpdateReport(val onSuccess: () -> Unit) : ReportIncidentUiEvent()
    data class DeleteReport(val onSuccess: () -> Unit) : ReportIncidentUiEvent()
}

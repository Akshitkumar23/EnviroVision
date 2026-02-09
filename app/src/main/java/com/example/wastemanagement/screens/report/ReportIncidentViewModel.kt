package com.example.wastemanagement.screens.report

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.BuildConfig
import com.example.wastemanagement.data.Incident
import com.example.wastemanagement.data.IncidentRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReportIncidentViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val auth: FirebaseAuth,
    private val geocoder: Geocoder,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var incidentType by mutableStateOf("")
    var description by mutableStateOf("")
    var severity by mutableStateOf("Medium")
    var location by mutableStateOf(LatLng(28.6139, 77.2090))
    var address by mutableStateOf("Fetching address...")
    var imageUris by mutableStateOf<List<Uri>>(emptyList())
    var isEditing by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var isAiLoading by mutableStateOf(false)

    val uiEvents = MutableSharedFlow<String>()

    private var incidentId: String? = null
    private val _loadedIncident = MutableStateFlow<Incident?>(null)

    // Initialize the GenerativeModel directly
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    init {
        incidentId = savedStateHandle.get<String>("incidentId")
        if (incidentId != null && incidentId!!.isNotBlank()) {
            isEditing = true
            loadIncident(incidentId!!)
        } else {
            fetchLastKnownLocation()
        }
    }

    fun generateWithAi() {
        if (description.isBlank()) {
            viewModelScope.launch { uiEvents.emit("Please enter a description first.") }
            return
        }
        viewModelScope.launch {
            isAiLoading = true
            try {
                val prompt = """
                Analyze the following user's description of a waste problem.
                Your task is to classify it into one of the following exact categories: [Illegal Dumping, Overflowing Bin, Damaged Bin, Littering, Hazardous Waste, Other].
                Also, estimate the severity as one of [Low, Medium, High] based on the description.
                Provide the response ONLY in a valid JSON format like this: {"category": "CATEGORY_NAME", "severity": "SEVERITY_LEVEL"}
                
                User Description: "$description"
                """
                val response: GenerateContentResponse = generativeModel.generateContent(prompt)
                val jsonResponse = response.text?.trim() ?: "{}"
                val jsonObject = JSONObject(jsonResponse)

                val category = jsonObject.optString("category", "Other")
                val estimatedSeverity = jsonObject.optString("severity", "Medium")

                incidentType = category
                severity = estimatedSeverity

            } catch (e: Exception) {
                uiEvents.emit("AI failed: ${e.message}")
                Log.e("AI_Error", "Error generating content", e)
            } finally {
                isAiLoading = false
            }
        }
    }

    private fun loadIncident(id: String) {
        viewModelScope.launch {
            incidentRepository.getIncidentById(id).collect { incident ->
                _loadedIncident.value = incident
                incident?.let {
                    incidentType = it.type
                    description = it.description
                    severity = it.severity
                    val locationParts = it.location.split(",")
                    if (locationParts.size == 2) {
                        val lat = locationParts[0].trim().toDoubleOrNull()
                        val lon = locationParts[1].trim().toDoubleOrNull()
                        if (lat != null && lon != null) {
                            val newLocation = LatLng(lat, lon)
                            location = newLocation
                            getAddressFromLocation(newLocation)
                        }
                    }
                    imageUris = it.imageUris.map { uriString -> uriString.toUri() }
                }
            }
        }
    }

    private fun fetchLastKnownLocation() {
        try {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    val newLocation = LatLng(loc.latitude, loc.longitude)
                    location = newLocation
                    getAddressFromLocation(newLocation)
                }
            }
        } catch (_: SecurityException) { /* Handled in UI */ }
    }

    private fun getAddressFromLocation(latLng: LatLng) {
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            address = if (addresses?.isNotEmpty() == true) {
                addresses[0].getAddressLine(0) ?: "Unknown address"
            } else {
                "Address not found"
            }
        } catch (e: Exception) {
            address = "Could not fetch address"
        }
    }

    fun onImagesSelected(uris: List<Uri>) {
        imageUris = imageUris + uris
    }

    fun onImageRemoved(uri: Uri) {
        imageUris = imageUris - uri
    }

    fun onLocationUpdate(newLocation: LatLng) {
        location = newLocation
        getAddressFromLocation(newLocation)
    }

    fun onDescriptionChange(newDescription: String) {
        description = newDescription
    }

    fun onSeverityChange(newSeverity: String) {
        severity = newSeverity
    }

    fun onIncidentTypeChange(newType: String) {
        incidentType = newType
    }

    fun submitOrUpdateReport(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                uiEvents.emit("CRITICAL: You are not signed in. Please restart the app.")
                return@launch
            }

            if (incidentType.isBlank() || description.isBlank()) {
                uiEvents.emit("Incident type and description cannot be empty.")
                return@launch
            }

            isLoading = true

            try {
                 // Simplified image handling for now
                val imageUrls = imageUris.map { it.toString() }
                val locationString = "${location.latitude},${location.longitude}"
                val currentTime = System.currentTimeMillis()
                val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(currentTime))

                val incidentToSave = Incident(
                    id = incidentId ?: UUID.randomUUID().toString(),
                    type = incidentType,
                    description = description,
                    location = locationString,
                    imageUris = imageUrls,
                    status = if(isEditing) _loadedIncident.value?.status ?: "Reported" else "Reported",
                    severity = severity,
                    timestamp = currentTime,
                    reportedBy = userId,
                    date = dateString
                )

                if (isEditing) {
                    incidentRepository.updateIncident(incidentToSave)
                    uiEvents.emit("Report updated successfully!")
                } else {
                    incidentRepository.insertIncident(incidentToSave)
                    uiEvents.emit("Report submitted successfully!")
                }
                onSuccess()
            } catch (e: Exception) {
                Log.e("SubmitReportError", "An error occurred", e)
                uiEvents.emit("Failed to save report: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteReport(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (incidentId != null) {
                incidentRepository.deleteIncidentById(incidentId!!)
                uiEvents.emit("Report deleted successfully!")
                onSuccess()
            }
        }
    }
}

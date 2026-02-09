package com.example.wastemanagement.screens.admindetail

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.IncidentRepository
import com.example.wastemanagement.data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminIncidentDetailViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val incidentId: String = savedStateHandle.get<String>("incidentId")!!

    private val _incident = MutableStateFlow<com.example.wastemanagement.data.Incident?>(null)
    val incident = _incident.asStateFlow()

    private val _reporter = MutableStateFlow<User?>(null)
    val reporter = _reporter.asStateFlow()

    private val _selectedStatus = MutableStateFlow("")
    val selectedStatus = _selectedStatus.asStateFlow()

    private val _afterPhotoUris = MutableStateFlow<List<Uri>>(emptyList())
    val afterPhotoUris = _afterPhotoUris.asStateFlow()

    init {
        loadIncidentDetails()
    }

    private fun loadIncidentDetails() {
        viewModelScope.launch {
            incidentRepository.getIncidentById(incidentId).collect {
                _incident.value = it
                _selectedStatus.value = it?.status ?: ""
                it?.reportedBy?.let { userId ->
                    fetchReporterDetails(userId)
                }
            }
        }
    }

    private fun fetchReporterDetails(userId: String) {
        viewModelScope.launch {
            try {
                val userDoc = firestore.collection("users").document(userId).get().await()
                _reporter.value = userDoc.toObject(User::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun onStatusSelected(status: String) {
        _selectedStatus.value = status
    }

    fun onAfterPhotosSelected(uris: List<Uri>) {
        _afterPhotoUris.value = _afterPhotoUris.value + uris
    }

    fun onAfterPhotoRemoved(uri: Uri) {
        _afterPhotoUris.value = _afterPhotoUris.value - uri
    }

    fun updateIncidentStatus(context: Context) {
        viewModelScope.launch {
            val currentIncident = _incident.value
            if (currentIncident != null) {
                try {
                    var uploadedPhotoUrl: String? = null
                    if (_afterPhotoUris.value.isNotEmpty()) {
                        val uri = _afterPhotoUris.value.first()
                        if (!uri.toString().startsWith("http")) {
                            val imageRef = storage.reference.child("after_photos/${System.currentTimeMillis()}_${uri.lastPathSegment}")
                            uploadedPhotoUrl = imageRef.putFile(uri).await().storage.downloadUrl.await().toString()
                        }
                    }

                    val updatedIncident = currentIncident.copy(
                        status = _selectedStatus.value,
                        afterImageUri = uploadedPhotoUrl ?: currentIncident.afterImageUri
                    )
                    incidentRepository.updateIncident(updatedIncident)
                    Toast.makeText(context, "Status updated successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to update status: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

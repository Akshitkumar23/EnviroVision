package com.example.wastemanagement.screens.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.data.User
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        fetchAllUsers()
    }

    private fun fetchAllUsers() {
        viewModelScope.launch {
            try {
                val result = firestore.collection("users").get().await()
                _users.value = result.toObjects(User::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    val filteredUsers: StateFlow<List<User>> = _users.combine(_searchQuery) { users, query ->
        if (query.isBlank()) {
            users
        } else {
            users.filter {
                it.displayName?.contains(query, ignoreCase = true) == true ||
                it.email?.contains(query, ignoreCase = true) == true
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun updateUserRole(uid: String, newRole: String) {
        viewModelScope.launch {
            try {
                firestore.collection("users").document(uid).update("role", newRole).await()
                // Refresh the user list after update
                fetchAllUsers()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

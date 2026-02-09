package com.example.wastemanagement.screens.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wastemanagement.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val role: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading) // Start with Loading
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _user = MutableStateFlow(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    var adminEmail by mutableStateOf("")
    var adminPassword by mutableStateOf("")

    // Check for existing user session on startup
    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                _authState.value = AuthState.Unauthenticated
            } else {
                try {
                    val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
                    val role = userDoc.getString("role") ?: "citizen"
                    _user.value = firebaseUser
                    _authState.value = AuthState.Authenticated(role)
                } catch (e: Exception) {
                    _error.value = "Failed to verify user role: ${e.message}"
                    signOut(null) // Sign out if role check fails
                }
            }
        }
    }

    fun onAdminEmailChange(value: String) { adminEmail = value }
    fun onAdminPasswordChange(value: String) { adminPassword = value }

    fun signInAsAdmin(onSuccess: () -> Unit) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            _error.value = "Email and password cannot be empty."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authResult = auth.signInWithEmailAndPassword(adminEmail, adminPassword).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    Log.d("AdminAuthCheck", "Login successful for UID: ${firebaseUser.uid}")
                    val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
                    if (userDoc.exists()) {
                        val userRole = userDoc.getString("role")
                        Log.d("AdminAuthCheck", "User document exists. Role from Firestore: $userRole")
                        if (userRole == "admin") {
                            _user.value = firebaseUser
                            _authState.value = AuthState.Authenticated("admin")
                            onSuccess()
                        } else {
                            _error.value = "You do not have admin privileges."
                            auth.signOut()
                        }
                    } else {
                        Log.w("AdminAuthCheck", "User document does NOT exist in Firestore for UID: ${firebaseUser.uid}")
                        _error.value = "Admin user record not found in database."
                        auth.signOut()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Admin login failed: ${e.message}"
                 Log.e("AdminAuthCheck", "An exception occurred during admin login", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signInWithGoogle(context: Context, onSignInSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .build()
                val request: GetCredentialRequest = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
                val result = credentialManager.getCredential(context, request)
                handleGoogleSignIn(result, onSignInSuccess)
            } catch (e: Exception) {
                _error.value = "An error occurred during Google sign in: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun handleGoogleSignIn(result: GetCredentialResponse, onSignInSuccess: () -> Unit) {
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken.idToken, null)

                viewModelScope.launch {
                    try {
                        val authResult = auth.signInWithCredential(firebaseCredential).await()
                        val firebaseUser = authResult.user
                        if (firebaseUser != null) {
                            val userDocRef = firestore.collection("users").document(firebaseUser.uid)
                            val userDoc = userDocRef.get().await()
                            val role = if (!userDoc.exists()) {
                                val newUser = hashMapOf("uid" to firebaseUser.uid, "displayName" to firebaseUser.displayName, "email" to firebaseUser.email, "role" to "citizen")
                                userDocRef.set(newUser).await()
                                "citizen"
                            } else {
                                userDoc.getString("role") ?: "citizen"
                            }
                            _user.value = firebaseUser
                            _authState.value = AuthState.Authenticated(role)
                            onSignInSuccess()
                        }
                    } finally {
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to handle Google sign in: ${e.message}"
                _isLoading.value = false
            }
        } else {
            _error.value = "Unexpected credential type after Google sign in"
            _isLoading.value = false
        }
    }

    fun signOut(context: Context?) {
        viewModelScope.launch {
            auth.signOut()
            context?.let {
                try {
                    val credentialManager = CredentialManager.create(it)
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                } catch (e: ClearCredentialException) {
                    Log.e("AuthViewModel", "Error clearing credentials on sign out", e)
                }
            }
            _user.value = null
            adminEmail = ""
            adminPassword = ""
            _error.value = null
            _authState.value = AuthState.Unauthenticated
        }
    }
}

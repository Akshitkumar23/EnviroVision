package com.example.wastemanagement.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wastemanagement.screens.login.AuthViewModel
import com.example.wastemanagement.screens.login.AuthState

@Composable
fun AuthRouter(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    val authState by authViewModel.authState.collectAsState()

    when (val state = authState) {
        is AuthState.Authenticated -> {
            val destination = if (state.role == "admin") "admin_dashboard" else "citizen_dashboard"
            navController.navigate(destination) {
                popUpTo("auth_router") { inclusive = true }
            }
        }
        else -> {
            // Stay on the role selection screen if not authenticated
            navController.navigate("role_selection") {
                 popUpTo("auth_router") { inclusive = true }
            }
        }
    }
}

package com.example.wastemanagement.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WelcomeMessage() {
    val currentDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Welcome, Citizen!", style = MaterialTheme.typography.headlineSmall)
        Text(currentDate, style = MaterialTheme.typography.bodyMedium)
    }
}

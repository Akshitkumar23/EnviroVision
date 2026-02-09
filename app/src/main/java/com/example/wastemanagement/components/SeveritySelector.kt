package com.example.wastemanagement.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SeveritySelector(
    selectedSeverity: String,
    onSeveritySelected: (String) -> Unit
) {
    val severities = listOf("Low", "Medium", "High")

    Column {
        Text("Severity Level", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            severities.forEach { severity ->
                val isSelected = selectedSeverity == severity
                val color = getSeverityColor(severity)

                OutlinedButton(
                    onClick = { onSeveritySelected(severity) },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(2.dp, if (isSelected) color else Color.Gray),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent,
                        contentColor = if (isSelected) color else Color.Gray
                    )
                ) {
                    Text(severity)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

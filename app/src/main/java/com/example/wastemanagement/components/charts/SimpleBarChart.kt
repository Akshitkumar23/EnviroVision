package com.example.wastemanagement.components.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SimpleBarChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
    colorProvider: (String) -> Color
) {
    if (data.isEmpty()) {
        Text("No data to display", modifier = modifier.padding(16.dp))
        return
    }
    
    val maxVal = data.values.maxOrNull() ?: 1

    Row(
        modifier = modifier.height(150.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        data.forEach { (label, value) ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(((value.toFloat() / maxVal) * 150).dp)
                        .background(colorProvider(label))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$label ($value)", 
                    style = MaterialTheme.typography.labelSmall, 
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

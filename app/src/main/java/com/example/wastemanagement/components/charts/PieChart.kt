package com.example.wastemanagement.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.wastemanagement.components.getTypeColor

@Composable
fun PieChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Text("No data to display", modifier = modifier.padding(16.dp))
        return
    }

    val total = data.values.sum()
    val proportions = data.values.map { it.toFloat() / total }
    val colors = data.keys.map { getTypeColor(it) }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(150.dp)) {
            var startAngle = -90f // Start from the top
            proportions.forEachIndexed { index, proportion ->
                val sweep = proportion * 360f
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )
                startAngle += sweep
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            data.keys.forEachIndexed { index, name ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(colors[index]))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$name (${data[name]})")
                }
            }
        }
    }
}

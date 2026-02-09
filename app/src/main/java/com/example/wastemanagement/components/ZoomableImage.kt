package com.example.wastemanagement.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

@Composable
fun ZoomableImage(model: Any) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures {
                    _, pan, zoom, _ ->
                    scale *= zoom
                    // Clamp the scale to a reasonable range
                    scale = scale.coerceIn(1f, 5f)

                    val newOffsetX = offsetX + pan.x
                    val newOffsetY = offsetY + pan.y

                    // Simple bounds check to prevent panning too far off-screen
                    val maxOffsetX = (scale - 1) * size.width / 2
                    val maxOffsetY = (scale - 1) * size.height / 2

                    offsetX = newOffsetX.coerceIn(-maxOffsetX, maxOffsetX)
                    offsetY = newOffsetY.coerceIn(-maxOffsetY, maxOffsetY)
                }
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = model),
            contentDescription = "Zoomable Image",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            contentScale = ContentScale.Fit
        )
    }
}

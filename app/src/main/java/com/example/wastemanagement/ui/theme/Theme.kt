package com.example.wastemanagement.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryBlue,
    tertiary = AccentTeal,
    background = Color(0xFF121212).copy(alpha = 0.8f), // 80% opaque dark background
    surface = Color(0xFF121212).copy(alpha = 0.8f),      // 80% opaque dark surface
    onPrimary = White,
    onSecondary = Black, 
    onTertiary = Black, 
    onBackground = White,
    onSurface = White,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryBlue,
    tertiary = AccentTeal,
    background = LightGrey.copy(alpha = 0.8f), // 80% opaque light grey background
    surface = White.copy(alpha = 0.8f),          // 80% opaque white surface
    onPrimary = White,
    onSecondary = Black, 
    onTertiary = Black, 
    onBackground = Black,
    onSurface = Black,
)

@Composable
fun WasteManagementTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

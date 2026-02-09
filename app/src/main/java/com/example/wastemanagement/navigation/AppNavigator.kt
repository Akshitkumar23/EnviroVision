package com.example.wastemanagement.navigation

import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * A singleton object to handle navigation events from outside the Composable hierarchy.
 * This allows non-UI components (like an Activity) to request navigation changes.
 */
object AppNavigator {
    val navigationEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
}

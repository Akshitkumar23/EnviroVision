package com.example.wastemanagement.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResolveIncidentDialog(
    onDismiss: () -> Unit,
    onSubmit: (afterImageUri: Uri, comment: String) -> Unit
) {
    var afterImageUri by remember { mutableStateOf<Uri?>(null) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mark as Resolved") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("To resolve this incident, please provide proof of work.")
                
                // Image Picker for a single "after" photo
                ImagePicker(
                    existingImageUris = listOfNotNull(afterImageUri),
                    onImagesSelected = { uris -> afterImageUri = uris.firstOrNull() },
                    onImageRemoved = { afterImageUri = null }
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Add a comment (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    afterImageUri?.let { uri ->
                        onSubmit(uri, comment)
                    }
                },
                enabled = afterImageUri != null
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

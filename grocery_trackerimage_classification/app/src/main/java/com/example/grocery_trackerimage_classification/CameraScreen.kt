package com.example.grocery_trackerimage_classification

import android.graphics.Bitmap
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.grocery_trackerimage_classification.classifier.GroceryClassifier
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Composable
fun CameraScreen(repository: StockRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. Generate ONE transaction ID when the screen opens
    val currentSessionTxnId = remember { repository.getCurrentTxnId() }

    // State to track items added in THIS session
    val sessionItems = remember { mutableStateListOf<Pair<String, Int>>() }

    var detectedLabel by remember { mutableStateOf("Scanning...") }
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // ... (Camera AndroidView code remains the same as previous)

        // UI Overlay
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Show list of items added so far in this transaction
            if (sessionItems.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text("In this Txn ($currentSessionTxnId): ${sessionItems.size} items", Modifier.padding(8.dp))
                }
            }

            Card {
                Text(text = "Detected: $detectedLabel", modifier = Modifier.padding(16.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                // Button 1: Add current item to list
                Button(onClick = { showDialog = true }) {
                    Text("Add Item")
                }

                // Button 2: Upload everything and end Transaction
                Button(
                    onClick = {
                        scope.launch {
                            // Upload the whole list at once
                            repository.addBatchStock(currentSessionTxnId, sessionItems.toList())
                            Toast.makeText(context, "All items uploaded!", Toast.LENGTH_SHORT).show()
                            sessionItems.clear()
                            // Only now we increment the ID for the next customer
                            // repository.incrementId() // Call this here instead
                        }
                    },
                    enabled = sessionItems.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Finish & Upload")
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add to Session?") },
                text = { Text("Add 1 unit of $detectedLabel?") },
                confirmButton = {
                    Button(onClick = {
                        // Add to local list instead of immediate upload
                        sessionItems.add(Pair(detectedLabel, 1))
                        showDialog = false
                        Toast.makeText(context, "Added to list", Toast.LENGTH_SHORT).show()
                    }) { Text("Add") }
                },
                dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
            )
        }
    }
}
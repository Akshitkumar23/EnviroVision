package com.example.wastemanagement.ml

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SmartReportHelper(
    private val context: Context,
    private val modelName: String = "gemma.tflite"
) {

    private var generativeModel: GenerativeModel? = null
    private var initializationJob: Job? = null

    init {
        // Start initialization in the background
        initializationJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                // Note: The new Gemini library doesn't require file-based loading for standard models.
                // This helper can be simplified if using online models, but we keep it for on-device.
                // For this example, we assume we might still load a custom model, but the primary
                // way will be via the Gemini SDK's modelName.
                
                // This part is now simplified as we are using a built-in model name with Gemini
                // You would typically initialize the model directly if not loading from a file.
            } catch (e: Exception) {
                System.err.println("Failed to initialize Generative Model: ${e.message}")
            }
        }
    }

    private suspend fun getInitializedModel(): GenerativeModel {
        // Wait for the initialization job to complete if it's running
        initializationJob?.join()
        if (generativeModel == null) {
            // Initialize it here if it hasn't been, assuming this is an on-demand call
            // For simplicity, we are assuming the ViewModel won't call generateReport until ready.
            // A more robust solution might use a StateFlow to represent readiness.
            throw IllegalStateException("Model is not initialized. Please ensure it is set up before calling.")
        }
        return generativeModel!!
    }

    suspend fun generateReport(userInput: String, apiKey: String): String {
        val model = GenerativeModel("gemini-1.5-flash", apiKey)
        
        val prompt = """
        You are an expert waste management assistant for an app.
        Your task is to analyze the user's description of a waste problem and classify it into ONE of the following exact categories:
        
        [Illegal Dumping, Overflowing Bin, Damaged Bin, Littering, Hazardous Waste, Other]
        
        Analyze the following user text and provide only the most appropriate category name as your response. Do not add any explanation or extra text.
        
        User Text: "$userInput"
        Category:
        """

        return withContext(Dispatchers.IO) {
            try {
                val response = model.generateContent(prompt)
                response.text?.trim() ?: "No response from AI"
            } catch (e: Exception) {
                "Error during inference: ${e.message}"
            }
        }
    }

    fun close() {
        // No-op for this version as the model is managed by the library
    }
}

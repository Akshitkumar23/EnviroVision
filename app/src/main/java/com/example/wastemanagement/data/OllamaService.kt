package com.example.wastemanagement.data

import retrofit2.http.Body
import retrofit2.http.POST

data class OllamaRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean = false
)

data class OllamaResponse(
    val response: String
    // Add other fields if needed, like 'done', 'context', etc.
)

interface OllamaService {
    @POST("api/generate")
    suspend fun generate(@Body request: OllamaRequest): OllamaResponse
}

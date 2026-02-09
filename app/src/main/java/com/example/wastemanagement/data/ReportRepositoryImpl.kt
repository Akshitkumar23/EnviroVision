package com.example.wastemanagement.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReportRepository {

    private val reportsCollection = firestore.collection("reports")

    override suspend fun submitReport(report: Report): Result<Unit> = try {
        // Generate a new ID for the report
        val newReportRef = reportsCollection.document()
        report.id = newReportRef.id // Assign the auto-generated ID

        newReportRef.set(report).await()
        Log.d("ReportRepository", "Report submitted successfully: ${report.id}")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("ReportRepository", "Error submitting report", e)
        Result.failure(e)
    }
}

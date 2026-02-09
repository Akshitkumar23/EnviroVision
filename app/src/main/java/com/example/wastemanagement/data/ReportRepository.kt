package com.example.wastemanagement.data

import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun submitReport(report: Report): Result<Unit>
    // We can add functions to get reports later
    // fun getUserReports(userId: String): Flow<List<Report>>
}

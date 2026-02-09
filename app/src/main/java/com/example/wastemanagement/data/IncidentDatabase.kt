package com.example.wastemanagement.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Incident::class, SmartBin::class], version = 3, exportSchema = false)
@TypeConverters(com.example.wastemanagement.data.TypeConverters::class)
abstract class IncidentDatabase : RoomDatabase() {
    abstract fun incidentDao(): IncidentDao
    abstract fun smartBinDao(): SmartBinDao
}

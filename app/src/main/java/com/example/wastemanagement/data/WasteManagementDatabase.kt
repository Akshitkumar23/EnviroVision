package com.example.wastemanagement.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Incident::class, Event::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WasteManagementDatabase : RoomDatabase() {
    abstract fun incidentDao(): IncidentDao
    abstract fun eventDao(): EventDao
}

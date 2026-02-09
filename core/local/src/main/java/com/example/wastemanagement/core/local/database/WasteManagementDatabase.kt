package com.example.wastemanagement.core.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wastemanagement.core.local.converters.Converters
import com.example.wastemanagement.core.local.dao.CategoryDao
import com.example.wastemanagement.core.local.models.CategoryEntity

@Database(entities = [CategoryEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class WasteManagementDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
}

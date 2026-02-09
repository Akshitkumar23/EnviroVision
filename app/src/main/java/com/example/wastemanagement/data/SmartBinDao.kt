package com.example.wastemanagement.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SmartBinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bins: List<SmartBin>)

    @Query("SELECT * FROM smart_bins")
    fun getAllBins(): Flow<List<SmartBin>>
    
}

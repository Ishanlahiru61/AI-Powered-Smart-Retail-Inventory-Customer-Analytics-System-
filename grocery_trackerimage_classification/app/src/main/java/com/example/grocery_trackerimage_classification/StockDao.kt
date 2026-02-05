package com.example.grocery_trackerimage_classification.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.grocery_trackerimage_classification.StockEntity

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: StockEntity)

    @Query("SELECT * FROM stock_table ORDER BY timestamp DESC")
    fun getAll(): Flow<List<StockEntity>>

    @Query("SELECT SUM(quantity) FROM stock_table")
    suspend fun totalQuantity(): Int?
}

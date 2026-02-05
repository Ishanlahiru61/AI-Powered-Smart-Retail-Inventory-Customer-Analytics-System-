package com.example.grocery_trackerimage_classification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = StockPreferences(application)
    val repository = StockRepository(prefs)

    // FIXED: Changed Triple to Pair
    fun uploadTransaction(txnId: String, items: List<Pair<String, Int>>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addBatchStock(txnId, items)
        }
    }

    suspend fun fetchAllStock(): List<StockEntity> = repository.getAllStock()
}
package com.example.grocery_trackerimage_classification

import android.content.Context

class StockPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("stock_prefs", Context.MODE_PRIVATE)

    // Gets the current ID to display/use, but doesn't change it yet
    fun getPreviewNextId(): String {
        val current = prefs.getInt("txn_counter", 600)
        return "TXN_$current"
    }

    // Actually moves the counter to the next number
    fun incrementId() {
        val current = prefs.getInt("txn_counter", 600)
        prefs.edit().putInt("txn_counter", current + 1).apply()
    }
}
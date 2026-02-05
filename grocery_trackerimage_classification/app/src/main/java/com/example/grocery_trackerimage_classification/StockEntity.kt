package com.example.grocery_trackerimage_classification

data class StockEntity(
    val id: String,            // MongoDB _id
    val transactionId: String,
    val productName: String,
    val quantity: Int,
    val timestamp: String      // No confidence field here
)
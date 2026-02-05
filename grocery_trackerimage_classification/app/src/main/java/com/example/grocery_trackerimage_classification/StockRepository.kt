package com.example.grocery_trackerimage_classification

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.bson.Document
import java.text.SimpleDateFormat
import java.util.*

class StockRepository(private val prefs: StockPreferences) {

    private val uri = "mongodb+srv://ishanlahiru1928_db_user:T5KU37EwFgCaW9Gg@cluster1.r3kjtnu.mongodb.net/?retryWrites=true&w=majority"

    private val client: MongoClient by lazy {
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(uri))
            .applyToClusterSettings { builder -> builder.srvMaxHosts(3) }
            .build()
        MongoClient.create(settings)
    }

    private val database: MongoDatabase by lazy { client.getDatabase("grocery_store") }
    private val collection: MongoCollection<Document> by lazy {
        database.getCollection<Document>("grocery_products")
    }

    fun getCurrentTxnId(): String = prefs.getPreviewNextId()

    // FIXED: Changed Triple back to Pair (Name, Quantity)
    suspend fun addBatchStock(transactionId: String, items: List<Pair<String, Int>>) = withContext(Dispatchers.IO) {
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val documents = items.map { (name, qty) ->
            Document()
                .append("transaction_id", transactionId)
                .append("product_name", name)
                .append("quantity", qty)
                .append("timestamp", currentTime)
        }
        try {
            if (documents.isNotEmpty()) {
                collection.insertMany(documents)
                prefs.incrementId()
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun getAllStock(): List<StockEntity> = withContext(Dispatchers.IO) {
        try {
            val documents = collection.find().toList()
            documents.map { doc ->
                StockEntity(
                    id = doc.getObjectId("_id").toString(),
                    transactionId = doc.getString("transaction_id") ?: "N/A",
                    productName = doc.getString("product_name") ?: "Unknown",
                    quantity = doc.getInteger("quantity") ?: 0,
                    timestamp = doc.getString("timestamp") ?: ""
                )
            }.reversed()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
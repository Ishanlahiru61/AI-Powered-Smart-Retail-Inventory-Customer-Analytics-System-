package com.example.grocery_trackerimage_classification.api

import com.example.grocery_trackerimage_classification.StockEntity
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MongoDBApi {

    @POST("action/insertOne")
    suspend fun insertStock(
        @Header("api-key") apiKey: String,
        @Body body: MongoInsertRequest
    ): Response<Unit>

    companion object {
        // You get this URL from the "Data API" tab in Atlas
        private const val BASE_URL = "https://data.mongodb-api.com/app/YOUR_APP_ID/endpoint/data/v1/"

        fun create(): MongoDBApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MongoDBApi::class.java)
        }
    }
}

data class MongoInsertRequest(
    val dataSource: String = "Cluster1",      // Your Cluster Name
    val database: String = "grocery_store",   // From your prompt
    val collection: String = "grocery_products", // From your prompt
    val document: StockEntity
)
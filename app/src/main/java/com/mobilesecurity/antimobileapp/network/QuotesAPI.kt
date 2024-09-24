package com.mobilesecurity.antimobileapp.network

import android.content.Context
import android.net.http.HttpException
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException

data class Quote(
    val _id: String,
    val author: String,
    val authorSlug: String,
    val content: String,
    val dateAdded: String,
    val dateModified: String,
    val length: Int,
    val tags: List<String>
)

interface RandomQuotesAPI {
    @GET("/quotes/random")
    suspend fun getRandomQuote(): Response<List<Quote>>
}

object RetrofitInstance {
    private var lastStoredQuote: Quote? = null

    object TimeManager {
        var quoteRequestCount = 0
        var lastRequestTimestamp: Long = System.currentTimeMillis()
        fun minutesElapsedSinceLastRequest(): Long {
            val currentTime = System.currentTimeMillis()
            val minutes = (currentTime - lastRequestTimestamp) / 1000 / 60
            return minutes
        }

        const val maxQuoteRequestsPerDay = 20

        fun isAboveQuota(): Boolean {
            return quoteRequestCount >= maxQuoteRequestsPerDay
        }
    }

    val api: RandomQuotesAPI by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RandomQuotesAPI::class.java)
    }
    val baseUrl = "https://api.quotable.io/"
    private suspend fun showToast(context: Context, message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    suspend fun getQuote(context: Context): Quote? {
        try {
            val response = api.getRandomQuote()
            if (response.isSuccessful) {

                // 1. manage quote request count
                TimeManager.quoteRequestCount += 1
                if (TimeManager.isAboveQuota()) {
                    showToast(context, "You have reached the quote request limit. Try again tomorrow.")
                    return lastStoredQuote
                }

                if (TimeManager.minutesElapsedSinceLastRequest() > 600) {
                    TimeManager.quoteRequestCount = 0
                    TimeManager.lastRequestTimestamp = System.currentTimeMillis()
                }


                // 2. get quote
                val body = response.body()!!
                val quote = body[0]
                lastStoredQuote = quote.copy()
                return quote
            }
        } catch (e: IOException) {
            Log.e("retrofit", "getQuote (IO error, no internet): ${e.message}")
            showToast(context, "Failed to get quote")

            return null
        } catch (e: HttpException) {
            Log.e("retrofit", "getQuote (Bad Request): ${e.message}")
            showToast(context, "Failed to get quote")

            return null
        }
        Log.e("retrofit", "getQuote (Unknown error)")
        showToast(context, "Failed to get quote")

        return null
    }
}

class QuotesAPI {
}
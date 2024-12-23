package com.example.sportsapplication

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.chromium.net.CronetEngine
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private val newsArticles = mutableListOf<Article>()
    private lateinit var cronetEngine: CronetEngine
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val apiKey = "YOUR_API_KEY_HERE"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up insets and layout adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(newsArticles)
        recyclerView.adapter = newsAdapter

        cronetEngine = CronetEngine.Builder(this).build()

        // Initialize Handler and start checking for updates every 10 seconds
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            if (isContentExpired()) {
                fetchNews()
            } else {
                loadCachedContent()
            }
            handler.postDelayed(runnable, 10_000)
        }
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    // bulk of work. fetch news establishes a connection to API and retrieves JSON in need of being parsed
    // after parsing through data make sure the article has an image url and choose a random one to display for 10 seconds
    // otherwise notify user with toast messages
    private fun fetchNews() {
        try {
            val requestBuilder = cronetEngine.newUrlRequestBuilder(
                "https://newsapi.org/v2/everything?q=NBA&apiKey=$apiKey",
                MyUrlRequestCallback { responseJson ->
                    if (responseJson != null) {
                        try {
                            Log.d("MainActivity", "JSON Response: $responseJson")
                            val newsResponse = Gson().fromJson(responseJson, NewsResponse::class.java)
                            runOnUiThread {
                                val matchingArticles = newsResponse.articles.filter { article ->
                                    article.urlToImage?.isNotEmpty() == true
                                }
                                val randomArticle = matchingArticles.randomOrNull()

                                if (randomArticle != null) {
                                    newsArticles.clear()
                                    newsArticles.add(randomArticle)
                                    newsAdapter.notifyDataSetChanged()

                                    // Cache new content and image URL
                                    saveContentToCache(randomArticle.content.orEmpty(), randomArticle.urlToImage)
                                } else {
                                    Toast.makeText(this, "No sports articles found", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Error parsing JSON or processing articles", e)
                            runOnUiThread {
                                Toast.makeText(this, "Failed to parse or process news data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Failed to load news", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                Executors.newSingleThreadExecutor()
            )

            val request = requestBuilder.build()
            request.start()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in fetchNews", e)
            Toast.makeText(this, "Error fetching news", Toast.LENGTH_SHORT).show()
        }
    }

    // saves the image as well as the content in their own text files in internal storage
    private fun saveContentToCache(content: String, imageUrl: String?) {
        // Save content
        openFileOutput("article_content.txt", Context.MODE_PRIVATE).use { outputStream ->
            outputStream.write(content.toByteArray())
        }
        // Save image URL so it can be read and displayed when needed from cache
        openFileOutput("article_image_url.txt", Context.MODE_PRIVATE).use { outputStream ->
            outputStream.write((imageUrl ?: "").toByteArray())
        }
        updateTimestamp()
    }

    // loads the content that has been saved to the cache and updates the newsAdapter to notify changes that should be displayed
    private fun loadCachedContent() {
        try {
            // Load content
            val contentBytes = openFileInput("article_content.txt").readBytes()
            val content = String(contentBytes)

            // Load image URL
            val imageUrlBytes = openFileInput("article_image_url.txt").readBytes()
            val imageUrl = String(imageUrlBytes).ifEmpty { null } // Use previous image URL if empty

            if (content.isNotEmpty()) {
                val cachedArticle = Article(
                    urlToImage = imageUrl,
                    content = content,
                    description = "This is cached content from a previous session."
                )
                newsArticles.clear()
                newsArticles.add(cachedArticle)
                newsAdapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to load cached content", Toast.LENGTH_SHORT).show()
        }
    }

    // returns a boolean and is used to determine if the content has been displayed for >10 seconds
    // if so return true and follow branch that calls fetchNews() to update content and refresh UI
    private fun isContentExpired(): Boolean {
        val sharedPreferences = getSharedPreferences("AppCachePrefs", Context.MODE_PRIVATE)
        val lastRetrievedTime = sharedPreferences.getLong("content_timestamp", 0L)
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastRetrievedTime) > 10_000 // 10 seconds
    }

    // this function updates the time stamp after everything is saved to cache
    private fun updateTimestamp() {
        val sharedPreferences = getSharedPreferences("AppCachePrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putLong("content_timestamp", System.currentTimeMillis())
            .apply()
    }
}

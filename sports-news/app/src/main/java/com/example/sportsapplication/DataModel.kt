package com.example.sportsapplication

data class NewsResponse(
    val articles: List<Article>
)

data class Article(
    val urlToImage: String?,
    val content: String?,
    val description: String?
)
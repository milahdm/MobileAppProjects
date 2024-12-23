package com.example.sportsapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class NewsAdapter(private val newsArticles: List<Article>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleImageView: ImageView = itemView.findViewById(R.id.articleImageView)
        val contentTextView: TextView = itemView.findViewById(R.id.articleContentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_items, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = newsArticles[position]
        // Use Glide or another image loading library to load images in RecyclerView
        Glide.with(holder.itemView.context)
            .load(article.urlToImage)
            .error(R.drawable.error_image)  // Replace with a drawable resource for errors
            .into(holder.articleImageView)

        holder.contentTextView.text = article.content ?: "Content Unavailable"
    }

    override fun getItemCount(): Int {
        return newsArticles.size
    }
}

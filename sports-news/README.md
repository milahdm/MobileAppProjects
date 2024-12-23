# News App

This is a news application built using Android Studio and Kotlin. The app fetches news articles related to specific topics using the [NewsAPI](https://newsapi.org/) and displays them in a user-friendly interface.

## Features
- Fetch and display news articles from NewsAPI.
- Interactive UI with a clean design.
- Utilizes RecyclerView for efficient list management.
- Built-in support for handling network requests.

## Setup Instructions

To run this project, you need to set up an API key from [NewsAPI](https://newsapi.org/).

### Steps to Set Up the API Key
1. Go to the [NewsAPI website](https://newsapi.org/) and sign up for an account (if you don’t already have one).
2. Generate an API key in your account dashboard.
3. Open the file `MainActivity.kt` in the project.
4. Locate the following line:
   ```kotlin
   private val apiKey = "YOUR_API_KEY_HERE"

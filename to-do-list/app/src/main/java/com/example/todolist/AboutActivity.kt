package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    private lateinit var imageViewTop: ImageView
    private lateinit var imageViewDev1: ImageView
    private lateinit var imageViewDev2: ImageView
    private lateinit var textViewDev1Title: TextView
    private lateinit var textViewDev1Description: TextView
    private lateinit var textViewDev2Title: TextView
    private lateinit var textViewDev2Description: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val buttonBack: Button = findViewById(R.id.button_back)
        buttonBack.setOnClickListener {
            finish()
        }

        // Link XML views to Kotlin variables
        imageViewTop = findViewById(R.id.imageView_top)
        imageViewDev1 = findViewById(R.id.imageView_dev1)
        imageViewDev2 = findViewById(R.id.imageView_dev2)
        textViewDev1Title = findViewById(R.id.textView_dev1_title)
        textViewDev1Description = findViewById(R.id.textView_dev1_description)
        textViewDev2Title = findViewById(R.id.textView_dev2_title)
        textViewDev2Description = findViewById(R.id.textView_dev2_description)


    }
}

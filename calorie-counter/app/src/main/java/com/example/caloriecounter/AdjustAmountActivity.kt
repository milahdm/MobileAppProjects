package com.example.caloriecounter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AdjustAmountActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var amountTextView: TextView
    private lateinit var increaseButton: Button
    private lateinit var decreaseButton: Button
    private lateinit var updateButton: Button

    private var amount = 0
    private var itemType = ""
    private var increment = 1  // Default increment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjust_amount)

        // Initialize views
        titleTextView = findViewById(R.id.titleTextView)
        amountTextView = findViewById(R.id.amountTextView)
        increaseButton = findViewById(R.id.increaseButton)
        decreaseButton = findViewById(R.id.decreaseButton)
        updateButton = findViewById(R.id.updateButton)

        // Get the item type and current amount from the intent
        itemType = intent.getStringExtra("item_type") ?: ""
        amount = intent.getIntExtra("current_amount", 0)

        // Set the title and increment based on item type
        when (itemType) {
            "meals" -> {
                titleTextView.text = "Adjust Meals (Calories)"
                increment = 10
            }
            "snacks" -> {
                titleTextView.text = "Adjust Snacks (Calories)"
                increment = 10
            }
            "water" -> {
                titleTextView.text = "Adjust Water (Cups)"
                increment = 1
            }
        }

        // Display the current amount
        amountTextView.text = amount.toString()

        // Increase amount
        increaseButton.setOnClickListener {
            amount += increment
            amountTextView.text = amount.toString()
        }

        // Decrease amount
        decreaseButton.setOnClickListener {
            if (amount >= increment) {
                amount -= increment
            } else {
                amount = 0
            }
            amountTextView.text = amount.toString()
        }

        // Update and return to the main screen
        updateButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("updated_amount", amount)
            resultIntent.putExtra("item_type", itemType)
            setResult(Activity.RESULT_OK, resultIntent)
            finish() // Close the activity and return to the main screen
        }
    }
}

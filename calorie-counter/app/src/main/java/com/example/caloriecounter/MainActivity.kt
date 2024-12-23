package com.example.caloriecounter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // UI elements
    private lateinit var dateEntry: EditText
    private lateinit var mealsCount: TextView
    private lateinit var updateMealsButton: Button
    private lateinit var waterCount: TextView
    private lateinit var updateWaterButton: Button
    private lateinit var snacksCount: TextView
    private lateinit var updateSnacksButton: Button

    // Constants for shared preferences and intents
    private val sharedPrefsFile = "calorie_counter_prefs"
    private val dateKey = "saved_date"
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    private val REQUEST_CODE_ADJUST_AMOUNT = 1

    /**
     * onCreate initializes the activity and sets up UI elements, listeners, and saved state restoration.
     *
     * @param savedInstanceState Bundle containing previously saved state (if any).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        dateEntry = findViewById(R.id.dateEntry)
        mealsCount = findViewById(R.id.mealsCount)
        updateMealsButton = findViewById(R.id.updateMealsButton)
        waterCount = findViewById(R.id.waterCount)
        updateWaterButton = findViewById(R.id.updateWaterButton)
        snacksCount = findViewById(R.id.snacksCount)
        updateSnacksButton = findViewById(R.id.updateSnacksButton)

        // Load saved date or use today's date
        loadSavedDate()

        // Save date when focus is lost
        dateEntry.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveDate()
            }
        }

        // Set click listeners for update buttons
        setUpdateButtonListener(updateMealsButton, "meals", mealsCount)
        setUpdateButtonListener(updateWaterButton, "water", waterCount)
        setUpdateButtonListener(updateSnacksButton, "snacks", snacksCount)
    }

    /**
     * Sets a click listener for a button to update a specific count (meals, water, snacks).
     *
     * @param button Button to set the listener on.
     * @param itemType Type of item (e.g., "meals", "water").
     * @param countView TextView displaying the current count.
     */
    private fun setUpdateButtonListener(button: Button, itemType: String, countView: TextView) {
        button.setOnClickListener {
            val currentAmount = countView.text.toString().toIntOrNull() ?: 0
            val intent = Intent(this, AdjustAmountActivity::class.java)
            intent.putExtra("item_type", itemType)
            intent.putExtra("current_amount", currentAmount)
            startActivityForResult(intent, REQUEST_CODE_ADJUST_AMOUNT)
        }
    }

    /**
     * Handles the result from the AdjustAmountActivity and updates the corresponding count.
     *
     * @param requestCode Request code for the activity result.
     * @param resultCode Result code returned by the activity.
     * @param data Intent containing the result data.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADJUST_AMOUNT && resultCode == RESULT_OK) {
            val updatedAmount = data?.getIntExtra("updated_amount", 0) ?: 0
            val itemType = data?.getStringExtra("item_type") ?: ""
            when (itemType) {
                "meals" -> {
                    mealsCount.text = updatedAmount.toString()
                    updateTotalCount()
                }
                "water" -> waterCount.text = updatedAmount.toString()
                "snacks" -> {
                    snacksCount.text = updatedAmount.toString()
                    updateTotalCount()
                }
            }
        }
    }

    /**
     * Updates the total count of calories by summing meals and snacks.
     */
    private fun updateTotalCount() {
        val mealsAmount = mealsCount.text.toString().toIntOrNull() ?: 0
        val snacksAmount = snacksCount.text.toString().toIntOrNull() ?: 0
        val totalAmount = mealsAmount + snacksAmount
        val totalCount = findViewById<TextView>(R.id.totalCount)
        totalCount.text = "$totalAmount"
    }

    /**
     * Loads the saved date from SharedPreferences or sets today's date if none is saved.
     */
    private fun loadSavedDate() {
        val sharedPrefs = getSharedPreferences(sharedPrefsFile, Context.MODE_PRIVATE)
        val savedDate = sharedPrefs.getString(dateKey, null)

        if (savedDate != null) {
            dateEntry.setText(savedDate)
        } else {
            val today = dateFormat.format(Calendar.getInstance().time)
            dateEntry.setText(today)
        }
    }

    /**
     * Saves the entered date to SharedPreferences.
     */
    private fun saveDate() {
        val date = dateEntry.text.toString()
        if (date.isNotEmpty()) {
            val sharedPrefs = getSharedPreferences(sharedPrefsFile, Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString(dateKey, date)
            editor.apply()
        }
    }

    /**
     * Saves the current state of the activity to a Bundle.
     *
     * @param outState Bundle to save the current state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("DATE_ENTRY", dateEntry.text.toString())
        outState.putInt("MEALS_COUNT", mealsCount.text.toString().toIntOrNull() ?: 0)
        outState.putInt("WATER_COUNT", waterCount.text.toString().toIntOrNull() ?: 0)
        outState.putInt("SNACKS_COUNT", snacksCount.text.toString().toIntOrNull() ?: 0)
    }

    /**
     * Restores the state of the activity from a Bundle.
     *
     * @param savedInstanceState Bundle containing the saved state.
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        dateEntry.setText(savedInstanceState.getString("DATE_ENTRY", ""))
        mealsCount.text = savedInstanceState.getInt("MEALS_COUNT", 0).toString()
        waterCount.text = savedInstanceState.getInt("WATER_COUNT", 0).toString()
        snacksCount.text = savedInstanceState.getInt("SNACKS_COUNT", 0).toString()
        updateTotalCount()
    }
}

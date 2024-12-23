package com.example.pizzaactivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private var totalCost = 0
    private var currentSaucePrice = 0 // to handle for switching sauces
    private var selectedToppingsCount = 0

    private var isDarkMode = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // intialize refereences
        val darkModeSwitch = findViewById<Switch>(R.id.darkModeSwitch)
        val layout = findViewById<LinearLayout>(R.id.mainLayout)


        val totalCostTextView = findViewById<TextView>(R.id.totalCost)

        val sauceSpinner = findViewById<Spinner>(R.id.sauceSpinner)
        val sauceOptions = arrayOf("Tomato Basil - $12", "Marinara - $10", "Barbecue - $15")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sauceOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sauceSpinner.adapter = adapter


        //
        // Handle sauce selection from Spinner
        sauceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> updateSauceCost(12, "Tomato Basil Sauce", totalCostTextView)
                    1 -> updateSauceCost(10, "Marinara Sauce", totalCostTextView)
                    2 -> updateSauceCost(15, "Barbecue Sauce", totalCostTextView)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }


        // initializing checkboxes for the toppings as a list
        val toppings = listOf(
            findViewById<CheckBox>(R.id.checkbox_chicken),
            findViewById<CheckBox>(R.id.checkbox_bacon),
            findViewById<CheckBox>(R.id.checkbox_pepperoni),
            findViewById<CheckBox>(R.id.checkbox_olives),
            findViewById<CheckBox>(R.id.checkbox_onions)
        )

        //enhanced for loop  to loop through list of toppings
        for (topping in toppings) {
            topping.setOnCheckedChangeListener { _, isChecked ->
                val price = if (isChecked) 1 else -1
                val action = if (isChecked) "added" else "removed"
                updateTotalCost(price, "${topping.text} $action", totalCostTextView)

                // Keep track of the number of selected toppings
                selectedToppingsCount += if (isChecked) 1 else -1
                if (selectedToppingsCount > 1) {
                    showToppingsDialog()
                }
            }
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Enable Dark Mode
                setDarkMode(layout, totalCostTextView)
            } else {
                // Disable Dark Mode
                setLightMode(layout, totalCostTextView)
            }
        }
    }



    /**
     * Updates the total cost of the pizza by adjusting for the selected sauce
     *
     * This function first subtracts the current sauce price from the total cost,
     * then adds the price of the newly selected sauce. It also displays a toast
     * message indicating which sauce was selected
     *
     * @param newSaucePrice The price of the newly selected sauce
     * @param sauceName The name of the sauce selected - used for displaying a toast message
     * @param totalCostTextView The TextView that displays the updated total cost of the pizza
     */
    private fun updateSauceCost(newSaucePrice: Int, sauceName: String, totalCostTextView: TextView) {
        // Subtract the current sauce price from total cost first
        totalCost -= currentSaucePrice
        // Update with the new sauce price
        currentSaucePrice = newSaucePrice
        totalCost += newSaucePrice
        totalCostTextView.text = "Total: $$totalCost"

        // Toast implementation
        val toast = Toast.makeText(this, "$sauceName selected", Toast.LENGTH_SHORT)
        toast.show()
    }



    /**
     * Updates the total cost of the pizza
     *
     * This function adds or subtracts the price of the item from the total cost
     * based on whether it is selected or deselected. It also updates the total cost displayed
     * in the provided TextView and shows a toast message indicating the action taken
     *
     * @param priceChange The change in price +1/-1
     * @param item The name of the item being added or removed for toast message
     * @param totalCostTextView The TextView that displays the updated total cost of the pizza
     */
    private fun updateTotalCost(priceChange: Int, item: String, totalCostTextView: TextView) {
        totalCost += priceChange
        totalCostTextView.text = "Total: $$totalCost"

        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(this, "$item", duration)
        toast.show()
    }

    private fun showToppingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Toppings Offer")
        builder.setMessage("Select 5 toppings for the price of 4.")
        builder.setPositiveButton("Confirmed") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }


    /**
     * Set Dark Mode
     */
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun setDarkMode(layout: LinearLayout, totalCostTextView: TextView) {
        layout.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundColorDark))
        totalCostTextView.setTextColor(ContextCompat.getColor(this, R.color.textColorDark))

        // Change the red text to a brighter red for sauce label
        val sauceLabel = findViewById<TextView>(R.id.sauceText)
        sauceLabel.setTextColor(ContextCompat.getColor(this, R.color.primaryRedBright))

        // Update the "Select your Toppings" text
        val toppingsLabel = findViewById<TextView>(R.id.toppingsText)
        toppingsLabel.setTextColor(ContextCompat.getColor(this, R.color.textColorDark))

        // Update the Spinner dropdown background and text color
        val sauceSpinner = findViewById<Spinner>(R.id.sauceSpinner)
        sauceSpinner.setPopupBackgroundResource(R.color.primaryColor) // Dark popup background
        sauceSpinner.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryColor)) // White text inside the spinner

        // Change checkbox text color to white for dark mode
        val toppings = listOf(
            findViewById<CheckBox>(R.id.checkbox_chicken),
            findViewById<CheckBox>(R.id.checkbox_bacon),
            findViewById<CheckBox>(R.id.checkbox_pepperoni),
            findViewById<CheckBox>(R.id.checkbox_olives),
            findViewById<CheckBox>(R.id.checkbox_onions)
        )
        for (topping in toppings) {
            topping.setTextColor(ContextCompat.getColor(this, R.color.checkboxDark)) // White text in dark mode
        }

        val darkModeSwitch = findViewById<Switch>(R.id.darkModeSwitch)
        darkModeSwitch.setTextColor(ContextCompat.getColor(this, R.color.textColorDark))
    }

    /**
     * Set Light Mode
     */
    private fun setLightMode(layout: LinearLayout, totalCostTextView: TextView) {
        layout.setBackgroundColor(ContextCompat.getColor(this, R.color.windowBackground))
        totalCostTextView.setTextColor(ContextCompat.getColor(this, R.color.textColor))

        // Revert the "Select your Sauce" text color to light mode
        val sauceLabel = findViewById<TextView>(R.id.sauceText)
        sauceLabel.setTextColor(ContextCompat.getColor(this, R.color.primaryColor))

        // Revert the "Select your Toppings" text color
        val toppingsLabel = findViewById<TextView>(R.id.toppingsText)
        toppingsLabel.setTextColor(ContextCompat.getColor(this, R.color.textColor))

        // Revert the spinner dropdown background and text color
        val sauceSpinner = findViewById<Spinner>(R.id.sauceSpinner)
        sauceSpinner.setPopupBackgroundResource(R.color.textColorDark)
        sauceSpinner.setBackgroundColor(ContextCompat.getColor(this, R.color.textColorDark)) // Black text for light mode

        // Revert checkbox text color to black for light mode
        val toppings = listOf(
            findViewById<CheckBox>(R.id.checkbox_chicken),
            findViewById<CheckBox>(R.id.checkbox_bacon),
            findViewById<CheckBox>(R.id.checkbox_pepperoni),
            findViewById<CheckBox>(R.id.checkbox_olives),
            findViewById<CheckBox>(R.id.checkbox_onions)
        )
        for (topping in toppings) {
            topping.setTextColor(ContextCompat.getColor(this, R.color.textColor)) // Black text in light mode
        }

        val darkModeSwitch = findViewById<Switch>(R.id.darkModeSwitch)
        darkModeSwitch.setTextColor(ContextCompat.getColor(this, R.color.textColor))
    }

}
package com.example.receiptscanner

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReceiptDetailActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var saveButton: Button

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private lateinit var receiptId: String
    private lateinit var userId: String
    private val items = mutableListOf<Item>()
    private lateinit var itemsAdapter: ItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_detail)

        titleEditText = findViewById(R.id.title_edit_text)
        dateEditText = findViewById(R.id.date_edit_text)
        itemsRecyclerView = findViewById(R.id.items_recycler_view)
        saveButton = findViewById(R.id.save_button)

        // Initialize RecyclerView
        itemsAdapter = ItemsAdapter(items, true)
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        itemsRecyclerView.adapter = itemsAdapter

        // Load receipt data
        val receipt = intent.getParcelableExtra<Receipt>("receipt")
        receiptId = intent.getStringExtra("receiptId") ?: ""
        userId = auth.currentUser?.uid ?: ""

        // Debugging: Log receiptId and userId
        Log.d("ReceiptDetail", "userId: $userId, receiptId: $receiptId")

        if (receiptId.isBlank()) {
            receiptId = db.collection("users").document(userId).collection("receipts").document().id
            Log.d("ReceiptDetail", "Generated new receiptId: $receiptId")
        }

        // Populate fields with existing receipt data
        receipt?.let {
            titleEditText.setText(it.title)
            dateEditText.setText(it.date)
            items.addAll(it.items)
            itemsAdapter.notifyDataSetChanged()
        }

        saveButton.setOnClickListener {
            saveEditedReceipt()
        }
    }

    private fun saveEditedReceipt() {
        val title = titleEditText.text.toString()
        val date = dateEditText.text.toString()
        val total = items.sumOf { it.price } // Calculate total dynamically

        if (title.isBlank() || date.isBlank()) {
            Toast.makeText(this, "Title and date cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        if (items.isEmpty()) {
            Toast.makeText(this, "Items list cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        // Log values for debugging
        Log.d("ReceiptDetail", "Saving receipt - userId: $userId, receiptId: $receiptId")
        Log.d("ReceiptDetail", "Title: $title, Date: $date, Total: $total, Items: $items")

        // Create updated receipt data
        val updatedReceipt = mapOf(
            "title" to title,
            "date" to date,
            "total" to total,
            "items" to items.map { mapOf("name" to it.name, "price" to it.price) }
        )

        // Save to Firestore
        db.collection("users").document(userId)
            .collection("receipts").document(receiptId)
            .set(updatedReceipt, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Receipt updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("ReceiptDetail", "Failed to save receipt: ${e.message}")
                Toast.makeText(this, "Failed to save receipt: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

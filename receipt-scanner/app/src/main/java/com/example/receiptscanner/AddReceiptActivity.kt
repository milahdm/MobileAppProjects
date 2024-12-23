package com.example.receiptscanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddReceiptActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var addItemButton: Button
    private lateinit var saveReceiptButton: Button
    private lateinit var scanButton: Button

    private val items = mutableListOf<Item>()
    private lateinit var itemsAdapter: ItemsAdapter
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private lateinit var scanReceiptLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        // Initialize views
        titleEditText = findViewById(R.id.receipt_title)
        dateEditText = findViewById(R.id.receipt_date)
        itemsRecyclerView = findViewById(R.id.items_recycler_view)
        addItemButton = findViewById(R.id.add_item_button)
        saveReceiptButton = findViewById(R.id.save_receipt_button)
        scanButton = findViewById(R.id.scan_button)

        // Setup RecyclerView
        itemsAdapter = ItemsAdapter(items, true)
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        itemsRecyclerView.adapter = itemsAdapter

        // Add item button logic
        addItemButton.setOnClickListener {
            itemsAdapter.addItem()
        }

        // Save receipt button logic
        saveReceiptButton.setOnClickListener {
            saveReceiptToFirestore()
        }

        // Setup ActivityResultLauncher for ScanActivity
        setupScanReceiptLauncher()

        // Scan button logic
        scanButton.setOnClickListener {
            launchScanReceiptActivity()
        }
    }

    private fun saveReceiptToFirestore() {
        val title = titleEditText.text.toString()
        val date = dateEditText.text.toString()
        val validItems = items.filter { it.name.isNotBlank() && it.price > 0 }
        val total = validItems.sumOf { it.price } // Calculate the total dynamically

        if (title.isBlank() || date.isBlank() || validItems.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields and add at least one valid item.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create receipt data
        val receipt = hashMapOf(
            "title" to title,
            "date" to date,
            "total" to total,
            "items" to validItems.map { mapOf("name" to it.name, "price" to it.price) }
        )

        Log.d("Firestore", "Saving receipt: $receipt")

        db.collection("users").document(userId).collection("receipts")
            .add(receipt)
            .addOnSuccessListener {
                Toast.makeText(this, "Receipt saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving receipt: ${e.message}", e)
                Toast.makeText(this, "Failed to save receipt: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupScanReceiptLauncher() {
        scanReceiptLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val receipt = result.data?.getParcelableExtra<Receipt>("receipt")
                if (receipt != null) {
                    populateScannedReceipt(receipt)
                } else {
                    Toast.makeText(this, "No receipt data returned.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun launchScanReceiptActivity() {
        val intent = Intent(this, ScanActivity::class.java)
        scanReceiptLauncher.launch(intent)
    }

    private fun populateScannedReceipt(receipt: Receipt) {
        titleEditText.setText(receipt.title)
        dateEditText.setText(receipt.date)
        items.clear()
        items.addAll(receipt.items)
        itemsAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Scanned receipt populated successfully!", Toast.LENGTH_SHORT).show()
    }
}

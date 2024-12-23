package com.example.receiptscanner

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var receiptRecyclerView: RecyclerView
    private lateinit var receiptAdapter: ReceiptAdapter
    private val receiptList = mutableListOf<Receipt>()
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val titleTextView: TextView = findViewById(R.id.title)
        titleTextView.text = "Your Receipts"

        receiptRecyclerView = findViewById(R.id.receipt_recycler_view)
        receiptAdapter = ReceiptAdapter(
            receipts = receiptList,
            onItemClick = { receipt -> navigateToReceiptDetail(receipt) },
            onDeleteClick = { receipt -> deleteReceiptFromFirestore(receipt) }
        )
        receiptRecyclerView.layoutManager = LinearLayoutManager(this)
        receiptRecyclerView.adapter = receiptAdapter

        fetchReceiptsFromFirestore()

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_bar_fragment, NavigationBarFragment().apply {
                onAddReceiptClicked = { launchAddReceiptActivity() }
                onReceiptsClicked = { reloadReceipts() }
                onLogoutClicked = { performLogout() }
            })
            .commit()
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
        }
    }

    private fun fetchReceiptsFromFirestore() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).collection("receipts")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Failed to fetch receipts: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Clear and rebuild the list without duplicates
                    val tempReceiptList = mutableListOf<Receipt>()
                    for (document in snapshot.documents) {
                        val title = document.getString("title") ?: "Untitled"
                        val date = document.getString("date") ?: "Unknown Date"
                        val items = (document.get("items") as? List<Map<String, Any>>)?.map {
                            Item(
                                name = it["name"] as String,
                                price = (it["price"] as? Double) ?: 0.0
                            )
                        }?.toMutableList() ?: mutableListOf()
                        val total = items.sumOf { it.price }
                        val receipt = Receipt(title, date, items, total)

                        if (!tempReceiptList.any { it.title == receipt.title && it.date == receipt.date }) {
                            tempReceiptList.add(receipt)
                        }
                    }
                    receiptList.clear()
                    receiptList.addAll(tempReceiptList)
                    receiptAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun deleteReceiptFromFirestore(receipt: Receipt) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId).collection("receipts")
            .whereEqualTo("title", receipt.title) 
            .whereEqualTo("date", receipt.date)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val receiptId = documents.documents[0].id
                    db.collection("users").document(userId).collection("receipts")
                        .document(receiptId)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Receipt deleted", Toast.LENGTH_SHORT).show()
                            receiptList.remove(receipt)
                            receiptAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to delete receipt: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    private fun navigateToReceiptDetail(receipt: Receipt) {
        val intent = Intent(this, ReceiptDetailActivity::class.java)
        intent.putExtra("receipt", receipt)
        startActivity(intent)
    }

    private fun launchAddReceiptActivity() {
        val intent = Intent(this, AddReceiptActivity::class.java)
        startActivity(intent)
    }

    private fun reloadReceipts() {
        fetchReceiptsFromFirestore()
        Toast.makeText(this, "Receipts reloaded.", Toast.LENGTH_SHORT).show()
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()
        redirectToLogin()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

package com.example.receiptscanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReceiptAdapter(
    private val receipts: MutableList<Receipt>,
    private val onItemClick: (Receipt) -> Unit, // Lambda for item click
    private val onDeleteClick: (Receipt) -> Unit
) : RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder>() {

    // ViewHolder class binds receipt data to item views
    inner class ReceiptViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.receipt_title)
        val dateTextView: TextView = view.findViewById(R.id.receipt_date)
        val totalTextView: TextView = view.findViewById(R.id.receipt_total)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_receipt, parent, false)
        return ReceiptViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receipt = receipts[position]
        holder.titleTextView.text = receipt.title
        holder.dateTextView.text = receipt.date
        holder.totalTextView.text = "Total: $${"%.2f".format(receipt.total)}"

        // Pass the click event to the provided lambda
        holder.itemView.setOnClickListener {
            onItemClick(receipt) // Pass the clicked receipt to the lambda
        }

        //delete
        holder.deleteButton.setOnClickListener {
            onDeleteClick(receipt)
            receipts.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int = receipts.size

    // Function to update the receipt list dynamically
    fun updateReceipts(newReceipts: List<Receipt>) {
        (receipts as MutableList).clear()
        receipts.addAll(newReceipts)
        notifyDataSetChanged()
    }
}

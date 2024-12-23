package com.example.receiptscanner

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(
    private val items: MutableList<Item>,
    private var isEditable: Boolean
) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameEditText: EditText = itemView.findViewById(R.id.item_name_edit)
        val priceEditText: EditText = itemView.findViewById(R.id.item_price_edit)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.edit_items, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        if (isEditable) {
            holder.nameEditText.visibility = View.VISIBLE
            holder.priceEditText.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE

            holder.nameEditText.setText(item.name)
            holder.priceEditText.setText(item.price.toString())

            // Update item name dynamically
            holder.nameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item.name = s.toString()
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            // Update item price dynamically
            holder.priceEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item.price = s.toString().toDoubleOrNull() ?: 0.0
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            holder.deleteButton.setOnClickListener {
                items.removeAt(position)
                notifyItemRemoved(position)
            }
        } else {
            holder.nameEditText.visibility = View.GONE
            holder.priceEditText.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size

    fun setEditable(editable: Boolean) {
        isEditable = editable
        notifyDataSetChanged()
    }

    fun addItem() {
        items.add(Item("", 0.0)) // Add a new empty item
        notifyItemInserted(items.size - 1)
    }

    fun getItems(): List<Item> = items // Method to fetch updated items
}

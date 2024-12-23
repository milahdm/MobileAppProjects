package com.example.receiptscanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class NavigationBarFragment : Fragment() {

    // Lambda functions for button actions
    var onAddReceiptClicked: (() -> Unit)? = null
    var onReceiptsClicked: (() -> Unit)? = null
    var onLogoutClicked: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_navi_bar, container, false)

        // Add Receipt Button
        val addButton: Button = view.findViewById(R.id.add_button)
        addButton.setOnClickListener {
            onAddReceiptClicked?.invoke()
        }

        // Receipts Button
        val receiptsButton: Button = view.findViewById(R.id.receipts_button)
        receiptsButton.setOnClickListener {
            onReceiptsClicked?.invoke()
        }


        // Logout Button
        val logoutButton: Button = view.findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            onLogoutClicked?.invoke()
        }

        return view
    }
}

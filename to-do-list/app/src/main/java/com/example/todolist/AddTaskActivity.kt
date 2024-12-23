package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AddTaskActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editTextTask: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        auth = FirebaseAuth.getInstance()

        // Check if the user is authenticated
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated. Redirecting to login.", Toast.LENGTH_SHORT).show()
            navigateToLogin()
            return
        }

        editTextTask = findViewById(R.id.editText_task)
        buttonSave = findViewById(R.id.button_save)
        buttonCancel = findViewById(R.id.button_cancel)

        // Save button listener
        buttonSave.setOnClickListener {
            val taskText = editTextTask.text.toString().trim()
            if (taskText.isEmpty() || taskText.length < 3) {
                Toast.makeText(this, "Task must be at least 3 characters", Toast.LENGTH_SHORT).show()
            } else {
                val resultIntent = Intent()
                resultIntent.putExtra("task", taskText)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

        buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}

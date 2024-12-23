package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class TaskSettingsActivity : AppCompatActivity() {

    private lateinit var textViewTask: TextView
    private lateinit var buttonMarkComplete: Button
    private lateinit var buttonCancel: Button
    private lateinit var buttonPreviousTask: Button
    private lateinit var buttonNextTask: Button

    private var taskList = mutableListOf<String>()
    private var currentPosition = 0

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_settings)

        auth = FirebaseAuth.getInstance()

        // Check if the user is authenticated
        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
            return
        }

        // Retrieve task list and current position from the intent
        taskList = intent.getStringArrayListExtra("task_list") ?: mutableListOf()
        currentPosition = intent.getIntExtra("position", 0)

        textViewTask = findViewById(R.id.textView_task)
        buttonMarkComplete = findViewById(R.id.button_mark_complete)
        buttonCancel = findViewById(R.id.button_cancel)
        buttonPreviousTask = findViewById(R.id.button_previous_task)
        buttonNextTask = findViewById(R.id.button_next_task)

        updateTaskText()

        // Mark as complete button
        buttonMarkComplete.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("task_position", currentPosition)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        buttonCancel.setOnClickListener {
            finish()
        }

        buttonPreviousTask.setOnClickListener {
            if (currentPosition > 0) {
                currentPosition--
                updateTaskText()
            } else {
                Toast.makeText(this, "No previous task", Toast.LENGTH_SHORT).show()
            }
        }
        
        buttonNextTask.setOnClickListener {
            if (currentPosition < taskList.size - 1) {
                currentPosition++
                updateTaskText()
            } else {
                Toast.makeText(this, "No next task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTaskText() {
        textViewTask.text = taskList[currentPosition]
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

package com.example.todolist

import TaskAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var buttonAddTask: Button
    private lateinit var buttonLogout: Button

    private val taskList = mutableListOf<String>()
    private lateinit var taskAdapter: TaskAdapter
    private val db = Firebase.firestore

    private lateinit var auth: FirebaseAuth
    private lateinit var searchBox: EditText
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Check authentication state
        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
            return
        }

        recyclerViewTasks = findViewById(R.id.recyclerView_tasks)
        buttonAddTask = findViewById(R.id.button_add_task)

        taskAdapter = TaskAdapter(taskList, { position ->
            // Handle task click
            val intent = Intent(this, TaskSettingsActivity::class.java)
            intent.putStringArrayListExtra("task_list", ArrayList(taskList))
            intent.putExtra("position", position)
            startActivityForResult(intent, REQUEST_CODE_TASK_SETTINGS)
        }, { position ->
            // Handle delete task
            deleteTaskFromFirestore(position)
        })

        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = taskAdapter

        fetchTasksFromFirestore()

        buttonAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_TASK)
        }

        buttonLogout = findViewById(R.id.button_logout)
        buttonLogout.setOnClickListener {
            auth.signOut()
            redirectToLogin()
        }

        searchBox = findViewById(R.id.search_box)
        searchButton = findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            val query = searchBox.text.toString().trim()
            if (query.isNotEmpty()) {
                searchTasks(query)
            } else {
                Toast.makeText(this, "Enter a search query", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun fetchTasksFromFirestore() {
        val userID = auth.currentUser?.uid
        if (userID == null) {
            Toast.makeText(this, "Error: User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userID).collection("tasks")
            .get()
            .addOnSuccessListener { result ->
                taskList.clear()
                for (document in result) {
                    val task = document.getString("task")
                    if (task != null) taskList.add(task)
                }
                taskAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching tasks: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteTaskFromFirestore(position: Int) {
        val userID = auth.currentUser?.uid
        if (userID == null) {
            Toast.makeText(this, "Error: User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val taskToDelete = taskList[position]

        db.collection("users").document(userID).collection("tasks")
            .whereEqualTo("task", taskToDelete)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    db.collection("users").document(userID).collection("tasks").document(document.id).delete()
                }
                taskList.removeAt(position)
                taskAdapter.notifyItemRemoved(position)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting task: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun searchTasks(query: String) {
        val userID = auth.currentUser?.uid
        if (userID == null) {
            Toast.makeText(this, "Error: User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userID).collection("tasks")
            .whereEqualTo("task", query)
            .get()
            .addOnSuccessListener { result ->
                val filteredTasks = result.mapNotNull { it.getString("task") }
                updateRecyclerView(filteredTasks)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error searching tasks: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateRecyclerView(filteredTasks: List<String>) {
        taskList.clear()
        taskList.addAll(filteredTasks)
        taskAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_TASK && resultCode == RESULT_OK) {
            val newTask = data?.getStringExtra("task")
            if (!newTask.isNullOrEmpty()) {
                addTaskToFirestore(newTask)
            }
        }
    }

    private fun addTaskToFirestore(task: String) {
        val userID = auth.currentUser?.uid
        if (userID == null) {
            Toast.makeText(this, "Error: User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val uniqueTaskID = db.collection("tasks").document().id
        val taskMap = hashMapOf(
            "id" to uniqueTaskID,
            "task" to task,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users").document(userID).collection("tasks").document(uniqueTaskID).set(taskMap)
            .addOnSuccessListener {
                taskList.add(task)
                taskAdapter.notifyItemInserted(taskList.size - 1)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding task: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val REQUEST_CODE_ADD_TASK = 1
        private const val REQUEST_CODE_TASK_SETTINGS = 2
    }
}

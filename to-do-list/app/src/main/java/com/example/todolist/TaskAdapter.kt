import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R

class TaskAdapter(
    private val taskList: MutableList<String>,
    private val onItemClick: (Int) -> Unit,
    private val onCheckmarkClick: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.task_title)
        private val checkBoxComplete: CheckBox = itemView.findViewById(R.id.checkBox_complete)

        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
            checkBoxComplete.setOnClickListener {
                onCheckmarkClick(adapterPosition)
            }
        }
    }

    //task item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskTitle.text = taskList[position]
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}
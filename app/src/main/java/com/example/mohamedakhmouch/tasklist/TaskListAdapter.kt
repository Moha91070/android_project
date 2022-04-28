import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mohamedakhmouch.R
import com.example.mohamedakhmouch.tasklist.Task

object ItemsDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return oldItem.id.equals(newItem.id) // comparison: are they the same "entity" ? (usually same id)
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return oldItem.equals(newItem)// comparison: are they the same "content" ? (simplified for data class)
    }
}
// l'IDE va râler ici car on a pas encore implémenté les méthodes nécessaires
class TaskListAdapter : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(ItemsDiffCallback) {
    // Déclaration de la variable lambda dans l'adapter:
    var onClickDelete: (Task) -> Unit = {}
    var onClickEdit: (Task) -> Unit = {}

    // on utilise `inner` ici afin d'avoir accès aux propriétés de l'adapter directement
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: Task) {
            // on affichera les données ici
           val textView = itemView.findViewById<TextView>(R.id.task_title)
            textView.text = task.title
            val textView2 = itemView.findViewById<TextView>(R.id.task_description)
            textView2.text = task.description
            val editButton = itemView.findViewById<ImageButton>(R.id.imageEditButton)
            editButton.setOnClickListener{onClickEdit(task)}
            val button = itemView.findViewById<ImageButton>(R.id.imageButton)
            button.setOnClickListener{onClickDelete(task)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)

        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
         holder.bind(this.currentList[position])
    }
}
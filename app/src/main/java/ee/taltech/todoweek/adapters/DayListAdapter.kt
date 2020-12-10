package ee.taltech.todoweek.adapters

import android.os.Build
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.*
import ee.taltech.todoweek.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import ee.taltech.todoweek.database.weekTaskList.Todo
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import ee.taltech.todoweek.model.CellClickListener
import kotlinx.android.synthetic.main.item_day_task.view.*
import java.time.LocalDateTime
import java.time.ZoneOffset

class DayListAdapter(var list: MutableList<Todo>, private val cellClickListener: CellClickListener) : RecyclerView.Adapter<ToDoDayViewHolder>() {
    lateinit var db: TodoDatabase

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoDayViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_hour_task, parent, false
        )
        db = TodoDatabase.getDatabase(itemView.context)
        return ToDoDayViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ToDoDayViewHolder, position: Int) {
        val dateFormat = DateTimeFormatter.ofPattern("dd.MMM.yyyy")
        val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
        val actionDate: LocalDateTime = Instant.ofEpochMilli(list[position].actionDate).atZone(ZoneOffset.UTC).toLocalDateTime()
        holder.time.text = actionDate.format(timeFormat)
        holder.priorityProgress.progress = list[position].priority.toInt()
        holder.message.movementMethod = ScrollingMovementMethod()

        val category = TodoDatabase.getDatabase(holder.itemView.context).todoCategoryDao().loadCategoryById(list[position].cid)
        if (category.size > 0) {
            holder.todoCategory.text = category.elementAt(0).name
        } else {
            holder.todoCategory.text = ""
        }
        holder.message.text = list[position].message
        holder.itemView.setOnClickListener {
            holder.actionDate = list[position].actionDate
            cellClickListener.onCellClickListener(position, holder)
        }
        holder.btn_delete.setOnClickListener {
            val dt = holder.itemView.context.getString(R.string.delete)
            MaterialAlertDialogBuilder(holder.itemView.context).setTitle("$dt").setMessage(R.string.todo_delete_confirm).setNeutralButton(R.string.cancel) { dialog, which ->
                // Respond to neutral button press
            }.setPositiveButton(R.string.delete) { dialog, which ->
                // Respond to positive button press
                db.todoDao().delete(list[position])
                list.removeAt(position)
                notifyItemRemoved(position)
                notifyDataSetChanged()
                Toast.makeText(holder.itemView.context, holder.itemView.context.resources.getString(R.string.todo_item_deleted), Toast.LENGTH_LONG).show()
                Log.e("deleteTodo: ", "deleted: $list[position]")
            }.show()
            db.todoDao().delete(list[position])
        }
    }
}

class ToDoDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val time: MaterialTextView = itemView.txt_time
    val todoCategory: MaterialTextView = itemView.txt_todo_category
    val message: MaterialTextView = itemView.txt_todo_message
    val btn_delete: MaterialButton = itemView.btn_delete_todo
    val priorityProgress: ProgressBar = itemView.progress_priority
    var actionDate: Long = -1L
}


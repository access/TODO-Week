package ee.taltech.todoweek.model

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.*
import ee.taltech.todoweek.R
import kotlinx.android.synthetic.main.item_week_task.view.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.util.Log
import ee.taltech.todoweek.database.weekTaskList.Todo
import kotlinx.android.synthetic.main.item_day_task.view.*

class AllToDoListAdapter(var list: MutableList<Todo>) : RecyclerView.Adapter<ToDoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_day_task, parent, false
        )
        return ToDoViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.date.text=list[position].actionDate.toString()
        holder.time.text=list[position].createDate.toString()
        holder.todoCategory.text = list[position].cid.toString()
        holder.message.text = list[position].message
    }


}

class ToDoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val date: MaterialTextView = itemView.txt_date_date
    val time: MaterialTextView = itemView.txt_time
    val todoCategory: MaterialTextView = itemView.txt_todo_category
    val message: MaterialTextView = itemView.txt_todo_message
}


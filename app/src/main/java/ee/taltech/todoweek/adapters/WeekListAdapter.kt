package ee.taltech.todoweek.adapters

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
import ee.taltech.todoweek.model.CellClickListener
import ee.taltech.todoweek.model.WeekDay
import java.time.ZoneOffset

class WeekListAdapter(var list: MutableList<WeekDay>, val cellClickListener: CellClickListener) : RecyclerView.Adapter<WeekDayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekDayViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_week_task, parent, false
        )
        return WeekDayViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: WeekDayViewHolder, position: Int) {
        if (list.count() > 0) {
            val currDate = System.currentTimeMillis()
            val dayDate: LocalDate = Instant.ofEpochMilli(list[position].actionDate).atZone(ZoneOffset.UTC).toLocalDate()
            val lastTodo = list[position].getNearTodo()
            var lastTodoTitle = ""
            Log.e("weekDayLastTodoCount: ", lastTodo.size.toString())
            if (lastTodo.count() > 0 && lastTodo.elementAt(0) != null) lastTodoTitle = list[position].getNearTodo().elementAt(0).message
            holder.dateDay.text = dayDate.format(DateTimeFormatter.ofPattern("d"))
            holder.dateMonth.text = dayDate.format(DateTimeFormatter.ofPattern("MMM"))
            holder.todoCountPerDay.text = "Day task count: " + list[position].taskCount().toString()
            holder.lastTodo.text = "Near task: " + lastTodoTitle
        }
        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(position, list[position]) // send WeekDay
        }
    }
}

class WeekDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dateDay: MaterialTextView = itemView.txt_date_day
    val dateMonth: MaterialTextView = itemView.txt_date_month
    val todoCountPerDay: MaterialTextView = itemView.txt_todo_count
    val lastTodo: MaterialTextView = itemView.txt_last_task
}


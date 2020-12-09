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

class WeekListAdapter(var list: MutableList<WeekDay>) : RecyclerView.Adapter<WeekDayViewHolder>() {

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
            val dayDate: LocalDate = Instant.ofEpochMilli(list[position].actionDate).atZone(ZoneId.systemDefault()).toLocalDate()


            val lastTodo = list[position].getLastTodo()
            var lastTodoTitle = ""
            Log.e("weekDayLastTodoCount: ", lastTodo.size.toString())
            if (lastTodo.count() > 0 && lastTodo.elementAt(0) != null) lastTodoTitle = list[position].getLastTodo().elementAt(0).message

            holder.dateDay.text = dayDate.format(DateTimeFormatter.ofPattern("d"))
            holder.dateMonth.text = dayDate.format(DateTimeFormatter.ofPattern("MMM"))
            holder.lastTodo.text = "Last ToDo: " + lastTodoTitle
            holder.todoCountPerDay.text = "ToDo`s count: " + list[position].taskCount().toString()
        }

//        val category = list[position]
//        holder.id.text = "${position+1}" //"${category.id}"
//        holder.name.text = "${category.name}"
//
//
//
////        holder.btn_delete.setTag(0, category)
//
////        holder.itemView.setOnClickListener {
////            cellClickListener.onCellClickListener(position, category)
////        }
//        holder.btn_delete.setOnClickListener {
//            val db = TodoDatabase.getDatabase(holder.itemView.context)
//            //Log.e("db: ", "created: ${db.toString()}")
//            val dt = holder.itemView.context.getString(R.string.delete)
//
//            val dao = db.todoCategoryDao()
//            MaterialAlertDialogBuilder(holder.itemView.context).setTitle("$dt - ${category.name}").setMessage(R.string.todo_delete_confirm)
//                .setNeutralButton(R.string.cancel) { dialog, which ->
//                    // Respond to neutral button press
//                }
////                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
////                    // Respond to negative button press
////                }
//                .setPositiveButton(R.string.delete) { dialog, which ->
//                    // Respond to positive button press
//                    dao.deleteCategory(category)
//                    list.removeAt(position)
//                    //val parentView =
////                    val vg = (it.parent.parent as ViewGroup)
//////                    val item = vg.findViewById<RelativeLayout>(R.id.view_holder)
//////                    if (item.parent != null) {
//////                        vg.removeView(item)
//////                        Log.e("viewRemove: ", "view removed")
//////                    }
////                    vg.removeView(it.parent as View)
//                    notifyItemRemoved(position)
//                    notifyDataSetChanged()
//                    Log.e("deleteCat: ", "deleted: $category")
////                    holder.itemView.recyclerView.apply {
//////                        setHasFixedSize(true)
////                        adapter = CategoriesAdapter(list, cellClickListener)
////                        layoutManager = LinearLayoutManager(this.context)
////                        addItemDecoration(
////                            DividerItemDecoration(
////                                this.context, DividerItemDecoration.VERTICAL
////                            )
////                        )
////                    }
//
//                }.show()
//        }
    }


}

class WeekDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dateDay: MaterialTextView = itemView.txt_date_day
    val dateMonth: MaterialTextView = itemView.txt_date_month
    val todoCountPerDay: MaterialTextView = itemView.txt_todo_count
    val lastTodo: MaterialTextView = itemView.txt_last_task
}


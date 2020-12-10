package ee.taltech.todoweek.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.*
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.weekTaskList.TodoCategory
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import kotlinx.android.synthetic.main.item_category_layout.view.*

class CategoriesAdapter(var list: MutableList<TodoCategory>) : RecyclerView.Adapter<CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_category_layout, parent, false
        )
        return CategoryViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = list[position]
        holder.id.text = "${position + 1}"
        holder.name.text = "${category.name}"
        holder.btn_delete.setOnClickListener {
            val db = TodoDatabase.getDatabase(holder.itemView.context)
            val dt = holder.itemView.context.getString(R.string.delete)
            val dao = db.todoCategoryDao()
            MaterialAlertDialogBuilder(holder.itemView.context).setTitle("$dt - ${category.name}").setMessage(R.string.todo_delete_confirm)
                .setNeutralButton(R.string.cancel) { dialog, which ->
                    // Respond to neutral button press
                }
//                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
//                    // Respond to negative button press
//                }
                .setPositiveButton(R.string.delete) { dialog, which ->
                    // Respond to positive button press
                    dao.deleteCategory(category)
                    list.removeAt(position)
                    notifyItemRemoved(position)
                    notifyDataSetChanged()
                    Log.e("deleteCat: ", "deleted: $category")
                }.show()
        }
    }
}

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val id: MaterialTextView = itemView.txt_id
    val name: MaterialTextView = itemView.txt_category_name
    val btn_delete: MaterialButton = itemView.btn_delete_category
}


package ee.taltech.todoweek.model

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.*
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.weekTaskList.TodoCategory
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import kotlinx.android.synthetic.main.categories_fragment.*
import kotlinx.android.synthetic.main.categories_fragment.view.*
import kotlinx.android.synthetic.main.item_category_layout.view.*


class CategoriesAdapter(var list: MutableList<TodoCategory>, private val cellClickListener: CellClickListener) :
    RecyclerView.Adapter<CategoryViewHolder>() {

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
        holder.id.text = "${position+1}" //"${category.id}"
        holder.name.text = "${category.name}"

//        holder.btn_delete.setTag(0, category)

//        holder.itemView.setOnClickListener {
//            cellClickListener.onCellClickListener(position, category)
//        }
        holder.btn_delete.setOnClickListener {
            val db = TodoDatabase.getDatabase(holder.itemView.context)
            //Log.e("db: ", "created: ${db.toString()}")
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
                    //val parentView =
//                    val vg = (it.parent.parent as ViewGroup)
////                    val item = vg.findViewById<RelativeLayout>(R.id.view_holder)
////                    if (item.parent != null) {
////                        vg.removeView(item)
////                        Log.e("viewRemove: ", "view removed")
////                    }
//                    vg.removeView(it.parent as View)
                    notifyItemRemoved(position)
                    notifyDataSetChanged()
                    Log.e("deleteCat: ", "deleted: $category")
//                    holder.itemView.recyclerView.apply {
////                        setHasFixedSize(true)
//                        adapter = CategoriesAdapter(list, cellClickListener)
//                        layoutManager = LinearLayoutManager(this.context)
//                        addItemDecoration(
//                            DividerItemDecoration(
//                                this.context, DividerItemDecoration.VERTICAL
//                            )
//                        )
//                    }

                }.show()
        }
    }


}

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val id: MaterialTextView = itemView.txt_id
    val name: MaterialTextView = itemView.txt_category_name
    val btn_delete: MaterialButton = itemView.btn_delete_category
}


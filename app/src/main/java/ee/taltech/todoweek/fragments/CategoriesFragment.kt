package ee.taltech.todoweek.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import ee.taltech.todoweek.adapters.CategoriesAdapter
import kotlinx.android.synthetic.main.add_todo.topBar
import kotlinx.android.synthetic.main.categories_fragment.*


class CategoriesFragment : Fragment() {
    lateinit var userDB: UsersDB
    lateinit var currentUser: UserModel
    lateinit var settingsDB: SettingsDBHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        settingsDB = SettingsDBHelper(this)
        userDB = UsersDB(this)

        val view = inflater.inflate(R.layout.categories_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = TodoDatabase.getDatabase(requireContext())
        //Log.e("db: ", "created: ${db.toString()}")
        val dao = db.todoCategoryDao()

        if (arguments != null) {
            val user = arguments?.get("user")!! as UserModel
            currentUser = user
            topBar.title = "${user.username} ${getResources().getString(R.string.todo_categories_title)}"
        }
        val btnAddCategory = view.findViewById<FloatingActionButton>(R.id.btn_add_category)

        btnAddCategory.setOnClickListener {
            gotoCategory(currentUser)
        }
        // fill categories recyclerview
        val cats = dao.loadCategories(currentUser.uid).toMutableList()
        val tmpContext = this

        recyclerView.apply {
            setHasFixedSize(true)
            adapter = CategoriesAdapter(cats)
            layoutManager = LinearLayoutManager(this.context)
            addItemDecoration(
                DividerItemDecoration(
                    this.context, DividerItemDecoration.VERTICAL
                )
            )
        }
        val btnBack = view.findViewById<MaterialButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            activity?.onBackPressed()
        }
//        val btnDeleteCategory = view.findViewById<MaterialButton>(R.id.btn_delete_category)
//        btnDeleteCategory?.setOnClickListener {
//            val category = it.getTag(0) as TodoCategory
//            if (category != null) {
//                MaterialAlertDialogBuilder(requireContext()).setTitle("${resources.getString(R.string.delete)} - ${category.name}")
//                    .setMessage(R.string.todo_delete_confirm).setNeutralButton(R.string.cancel) { dialog, which ->
//                        // Respond to neutral button press
//                    }
////                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
////                    // Respond to negative button press
////                }
//                    .setPositiveButton(R.string.delete) { dialog, which ->
//                        // Respond to positive button press
//                        dao.deleteCategory(category)
//                        reloadCurrentFragment()
//                        Toast.makeText(context, getResources().getString(R.string.todo_category_deleted), Toast.LENGTH_LONG).show()
//                    }.show()
//            }
//        }

//        btnDeleteCategory?.setOnClickListener {
//            val category = it.getTag(0) as TodoCategory
//            if (category != null) {
//                dao.deleteCategory(category)
//                Log.e("addCategory: ", "added: $category")
//                val all = dao.loadCategories(currentUser.uid)
//                for (el in all) {
//                    Log.e("category: ", "val: ${el.toString()}")
//                }
//                Toast.makeText(context, getResources().getString(R.string.todo_category_deleted), Toast.LENGTH_LONG).show()
//                reloadCurrentFragment()
//            }
//        }
    }

    private fun reloadCurrentFragment() {
        val transaction = fragmentManager?.beginTransaction()?.replace(R.id.container, this)
        transaction?.commit()
    }

    private fun closeThisFragment() {
        activity?.onBackPressed()
    }

    fun gotoCategory(user: UserModel) {
        val reqFragment = CategoryFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", user)
        reqFragment.arguments = bundle
        navigateTo(reqFragment, true)
    }

    private fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = fragmentManager?.beginTransaction()?.replace(R.id.container, fragment)
        if (addToBackstack) {
            transaction?.addToBackStack(null)
        }
        transaction?.commit()
    }
}

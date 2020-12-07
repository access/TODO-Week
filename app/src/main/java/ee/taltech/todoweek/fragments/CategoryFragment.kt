package ee.taltech.todoweek.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
import ee.taltech.todoweek.database.weekTaskList.TodoCategory
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import kotlinx.android.synthetic.main.add_todo.*
import kotlinx.android.synthetic.main.add_todo.topBar
import kotlinx.android.synthetic.main.categories_fragment.*


class CategoryFragment : Fragment() {
    lateinit var userDB: UsersDB
    lateinit var currentUser: UserModel
    lateinit var settingsDB: SettingsDBHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        settingsDB = SettingsDBHelper(this)
        userDB = UsersDB(this)

        val view = inflater.inflate(R.layout.category_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (arguments != null) {
            val user = arguments?.get("user")!! as UserModel
            currentUser=user
        }
        val btnCancel = view.findViewById<MaterialButton>(R.id.btn_cancel)
        btnCancel.setOnClickListener{
            closeThisFragment()
        }
        val btnAdd = view.findViewById<MaterialButton>(R.id.btn_add_category)
        btnAdd.setOnClickListener {
            val db = TodoDatabase.getDatabase(requireContext())
            //Log.e("db: ", "created: ${db.toString()}")
            val dao = db.todoCategoryDao()
            val txtCategory = view.findViewById<TextInputEditText>(R.id.txt_category_name)
            val category = TodoCategory(0, currentUser.uid,txtCategory.text.toString())
            dao.addCategory(category)
            Log.e("addCategory: ", "added: $category")
            val all = dao.loadCategories(currentUser.uid)
            for (el in all) {
                Log.e("todo: ", "val: ${el.toString()}")
            }
        }
    }

    private fun closeThisFragment() {
        activity?.onBackPressed()
    }

    fun gotoWeekTasks(user: UserModel) {
        val weekFrag = WeekListFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", user)
        weekFrag.arguments = bundle
        navigateTo(weekFrag, true)
    }

    private fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = fragmentManager?.beginTransaction()?.replace(R.id.container, fragment)
        if (addToBackstack) {
            transaction?.addToBackStack(null)
        }
        transaction?.commit()
    }
}

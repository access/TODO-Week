package ee.taltech.todoweek.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
import ee.taltech.todoweek.database.weekTaskList.TodoCategory
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase


class CategoryFragment : Fragment() {
    private lateinit var userDB: UsersDB
    private lateinit var currentUser: UserModel
    private lateinit var settingsDB: SettingsDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        settingsDB = SettingsDBHelper(this)
        userDB = UsersDB(this)
        return inflater.inflate(R.layout.category_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val user = arguments?.get("user")!! as UserModel
            currentUser = user
        }
        val btnCancel = view.findViewById<MaterialButton>(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            closeThisFragment()
        }
        val btnAdd = view.findViewById<MaterialButton>(R.id.btn_add_category)
        btnAdd.setOnClickListener {
            val db = TodoDatabase.getDatabase(requireContext())
            //Log.e("db: ", "created: ${db.toString()}")
            val dao = db.todoCategoryDao()
            val txtCategory = view.findViewById<TextInputEditText>(R.id.txt_category_name)
            val inputText = txtCategory.text.toString()
            if (inputText.isNotEmpty()) {
                val category = TodoCategory(0, currentUser.uid, inputText)
                dao.addCategory(category)
                Log.e("addCategory: ", "added: $category")
                Toast.makeText(requireContext(), resources.getString(R.string.todo_category_added), Toast.LENGTH_LONG).show()
            }
            val all = dao.loadCategories(currentUser.uid)
            for (el in all) {
                Log.e("category: ", "$el")
            }
            closeThisFragment()
        }
    }

    private fun closeThisFragment() {
        activity?.onBackPressed()
    }

    private fun gotoCategories(user: UserModel) {
        val reqFragment = CategoriesFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", user)
        reqFragment.arguments = bundle
        navigateTo(reqFragment, true)
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

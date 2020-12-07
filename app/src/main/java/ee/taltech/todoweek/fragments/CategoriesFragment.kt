package ee.taltech.todoweek.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
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


        if (arguments != null) {
            val user = arguments?.get("user")!! as UserModel
            currentUser=user
            topBar.title = "${user.username} ${getResources().getString(R.string.todo_categories_title)}"
        }
        val btnAddCategory = view.findViewById<ExtendedFloatingActionButton>(R.id.btn_add_category)

        btnAddCategory.setOnClickListener {
            gotoCategory(currentUser)
        }
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

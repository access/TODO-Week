package ee.taltech.todoweek.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
import kotlinx.android.synthetic.main.weeklist_fragment.*

class WeekListFragment : Fragment() {
    lateinit var userDB: UsersDB
    lateinit var currentUser: UserModel
    lateinit var settingsDB: SettingsDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        settingsDB = SettingsDBHelper(this)
        userDB = UsersDB(this)

        val view = inflater.inflate(R.layout.weeklist_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val user = arguments?.get("user")!! as UserModel
            if (user.uid >= 0) { //correct user

            }
            btn_add_todo.setOnClickListener {
                gotoAddTodo(user)
            }

            view.findViewById<MaterialToolbar>(R.id.topBar).title = "${user.username} ${getResources().getString(R.string.week_tasks)}"
        }
    }

    fun gotoAddTodo(user: UserModel) {
        val nextFragment = AddTodoFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", user)
        nextFragment.arguments = bundle
        navigateTo(nextFragment, true)
    }

    private fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = fragmentManager?.beginTransaction()?.replace(R.id.container, fragment)
        if (addToBackstack) {
            transaction?.addToBackStack(null)
        }
        transaction?.commit()
    }

}

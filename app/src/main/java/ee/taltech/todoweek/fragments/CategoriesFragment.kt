package ee.taltech.todoweek.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
import kotlinx.android.synthetic.main.add_todo.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class CategoriesFragment : Fragment() {
    lateinit var userDB: UsersDB
    lateinit var currentUser: UserModel
    lateinit var settingsDB: SettingsDBHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        settingsDB = SettingsDBHelper(this)
        userDB = UsersDB(this)

        val view = inflater.inflate(R.layout.add_todo, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (arguments != null) {
            val user = arguments?.get("user")!! as UserModel
            topBar.title = "${user.username} ${getResources().getString(R.string.todo_addtodo)}"

        }
        btn_cancel.setOnClickListener {
            closeThisFragment()
        }
        btn_save_todo.setOnClickListener {
        }
        val c = arrayOf("Belgium", "France", "Italy", "Germany", "Spain")
//        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
//            requireContext(), R.layout.add_todo, c
//        )
        val adapter = ArrayAdapter(requireContext(), R.layout.textview_item_categories, c)

        val textView = view.findViewById<AutoCompleteTextView>(R.id.category_list)
        textView.threshold = 1
        textView.setAdapter(adapter)
        textView.setOnItemClickListener { parent, view, position, id ->
            Log.e("catId: ", "id:$id position:$position text:${textView.text}")

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

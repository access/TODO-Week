package ee.taltech.todoweek.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import ee.taltech.todoweek.model.AllToDoListAdapter
import ee.taltech.todoweek.model.WeekDay
import ee.taltech.todoweek.model.WeekListAdapter
import kotlinx.android.synthetic.main.add_todo.*
import kotlinx.android.synthetic.main.weeklist_fragment.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


class AllToDoListFragment : Fragment() {
    lateinit var userDB: UsersDB
    lateinit var currentUser: UserModel
    lateinit var settingsDB: SettingsDBHelper
    lateinit var db: TodoDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        settingsDB = SettingsDBHelper(this)
        userDB = UsersDB(this)
        db = TodoDatabase.getDatabase(requireContext())
        //Log.e("db: ", "created: ${db.toString()}")
        val view = inflater.inflate(R.layout.all_todo_fragment, container, false)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val user = arguments?.get("user")!! as UserModel
            currentUser=user
            if (user.uid >= 0) { //correct user
                val dao = db.todoDao()
                // draw recyclerview as fill list by days from today to 7 days forward
                // init today point
                val currDate = System.currentTimeMillis()
                val startDate: LocalDate = Instant.ofEpochMilli(currDate).atZone(ZoneId.systemDefault()).toLocalDate()
                var weekDayList: MutableList<WeekDay> = ArrayList()
//                for (idx in 1..7) {
//                    Log.e("startDate: ", "$startDate")
//                    val reqDate = startDate.plusDays(idx - 1L).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
//                    Log.e("reqDate: ", "$reqDate")
//                    weekDayList.add(WeekDay(reqDate, currentUser.uid, requireContext()))
//                }

                val allTodo = dao.getAll().toMutableList()
                for(todo in allTodo){
                    weekDayList.add(WeekDay(todo.actionDate, currentUser.uid, requireContext()))
                }

                recycler_week_tasks.apply {
                    setHasFixedSize(true)
                    adapter = AllToDoListAdapter(allTodo )
                    layoutManager = LinearLayoutManager(requireContext())
                    addItemDecoration(
                        DividerItemDecoration(
                            requireContext(), DividerItemDecoration.VERTICAL
                        )
                    )
                }


            }
            btn_add_todo.setOnClickListener {
                gotoAddTodo(user)
            }

            view.findViewById<MaterialToolbar>(R.id.topBar).title = "${user.username} ${getResources().getString(R.string.week_tasks)}"
        }

        btn_cancel.setOnClickListener {
            activity?.onBackPressed()
        }
    }


    private fun gotoAddTodo(user: UserModel) {
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

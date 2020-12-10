package ee.taltech.todoweek.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import ee.taltech.todoweek.R
import ee.taltech.todoweek.adapters.AllToDoListAdapter
import ee.taltech.todoweek.adapters.ToDoViewHolder
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
import ee.taltech.todoweek.database.weekTaskList.Todo
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import ee.taltech.todoweek.model.*
import kotlinx.android.synthetic.main.add_todo.*
import kotlinx.android.synthetic.main.weeklist_fragment.*

class AllToDoListFragment : Fragment(), CellClickListener {
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
        val view = inflater.inflate(R.layout.all_todo_fragment, container, false)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val user = arguments?.get("user")!! as UserModel
            currentUser = user
            if (user.uid >= 0) { //correct user
                val navBar = view.findViewById<MaterialToolbar>(R.id.navBar)
                navBar.title = "${user.username} | ${resources.getString(R.string.todo_weekday_show_all)}"


                val dao = db.todoDao()
                // draw recyclerview as fill list by days from today to 7 days forward
                // init today point
                val currDate = System.currentTimeMillis()
                var weekDayList: MutableList<WeekDay> = ArrayList()
                val allTodo = dao.getAllUserTodos(currentUser.uid).toMutableList()
                for (todo in allTodo) {
                    weekDayList.add(WeekDay(todo.actionDate, currentUser.uid, requireContext()))
                }
                val thisContext = this
                recycler_week_tasks.apply {
                    setHasFixedSize(true)
                    adapter = AllToDoListAdapter(allTodo, thisContext)
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

    private fun gotoEditTodo(user: UserModel, todo: Todo) {
        val nextFragment = AddTodoFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", user)
        bundle.putParcelable("todo", todo)
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

    override fun onCellClickListener(position: Int, toDoViewHolder: Any) {
        val holder = toDoViewHolder as ToDoViewHolder
        val reqTodo = db.todoDao().loadSingleById(holder.todoId)
        Log.e("todo: ", reqTodo.toString())
        gotoEditTodo(currentUser, reqTodo)
    }

}

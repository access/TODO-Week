package ee.taltech.todoweek.fragments

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
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
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import ee.taltech.todoweek.model.WeekDay
import ee.taltech.todoweek.adapters.WeekListAdapter
import ee.taltech.todoweek.model.CellClickListener
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.weeklist_fragment.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset


class WeekListFragment : CellClickListener, Fragment() {
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
        val view = inflater.inflate(R.layout.weeklist_fragment, container, false)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val user = arguments?.get("user")!! as UserModel
            currentUser = user
            if (user.uid >= 0) { //correct user
                // draw recyclerview as fill list by days from today to 7 days forward
                // init today point
                val currDate = System.currentTimeMillis()
                val startDate: LocalDate = Instant.ofEpochMilli(currDate).atZone(ZoneOffset.UTC).toLocalDate()
                Log.e("startDate: ", "$startDate")
                var weekDayList: MutableList<WeekDay> = ArrayList()
                for (idx in 1..7) {
                    val reqDate = startDate.plusDays(idx - 1L).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                    Log.e("reqDate: ", "$reqDate date:${startDate.plusDays(idx - 1L).atStartOfDay(ZoneOffset.UTC).toString()}")
                    weekDayList.add(WeekDay(reqDate, currentUser.uid, requireContext()))
                }
                val thisContext = this

                recycler_week_tasks.apply {
                    setHasFixedSize(true)
                    adapter = WeekListAdapter(weekDayList, thisContext)
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

            btn_show_all.setOnClickListener {
                gotoAllTodo(currentUser)
            }

            val navBar = view.findViewById<MaterialToolbar>(R.id.topBar)
            navBar.setOnMenuItemClickListener { item ->
                if (true) {
                    Log.e("clickedTopBar: ", "$item")
                    for (todo in db.todoDao().getAll()) {
                        Log.e("dbTodo: ", "$todo")
                    }
                    true
                } else false
            }
        }
    }

    private fun gotoAddTodo(user: UserModel) {
        val nextFragment = AddTodoFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", user)
        nextFragment.arguments = bundle
        navigateTo(nextFragment, true)
    }

    private fun gotoAllTodo(user: UserModel) {
        val nextFragment = AllToDoListFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", user)
        nextFragment.arguments = bundle
        navigateTo(nextFragment, true)
    }

    private fun gotoDayList(user: UserModel, sendData: SendData) {
        val nextFragment = DayListFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", user)
        bundle.putParcelable("date", sendData)
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

    override fun onCellClickListener(position: Int, attributes: Any) {
        val weekDay = attributes as WeekDay
        Log.e("weekDay: ", "$weekDay lastTodo: ${weekDay.getNearTodo().elementAt(0)}")
        gotoDayList(currentUser, SendData(weekDay.actionDate))
    }

}

@Parcelize
data class SendData(val actionDate: Long) : Parcelable
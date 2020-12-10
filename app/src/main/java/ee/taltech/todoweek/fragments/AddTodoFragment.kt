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
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
import ee.taltech.todoweek.database.weekTaskList.Todo
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import kotlinx.android.synthetic.main.add_todo.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


class AddTodoFragment : Fragment() {
    private lateinit var userDB: UsersDB
    private lateinit var currentUser: UserModel
    private lateinit var settingsDB: SettingsDBHelper
    private var currentToDo: Todo? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        settingsDB = SettingsDBHelper(this)
        userDB = UsersDB(this)

        val view = inflater.inflate(R.layout.add_todo, container, false)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var timePickerHour: Int = -1
        var timePickerMinute: Int = -1
        var year: Int = -1
        var month: Int = -1
        var day: Int = -1
        var categoryId: Int = -1
        var todoPriority: Double = -1.0
        var todoMessage: String = ""
        var actionDate: Long = -1

        if (arguments != null) {
            val user = arguments?.get("user")!! as UserModel
            val todo = if (arguments!!.get("todo") == null) null else arguments?.get("todo")!! as Todo
            if (user != null) {
                currentUser = user
                // check if edit existing todo
                if (todo != null) {
                    currentToDo = todo
                    topBar.title = "${user.username} | ${resources.getString(R.string.todo_edit_todo)}"
                    // fill data to controls
                    val categoryControl = view.findViewById<TextInputLayout>(R.id.categoryLayout) // category
                    val btnTime = view.findViewById<MaterialButton>(R.id.btn_time)
                    val btnDate = view.findViewById<MaterialButton>(R.id.btn_date)
                    val sliderPriority = view.findViewById<Slider>(R.id.sliderPriority)
                    val message = view.findViewById<TextInputEditText>(R.id.txt_msg)
                    // ------------------------
                    val category = TodoDatabase.getDatabase(requireContext()).todoCategoryDao().loadCategoryById(todo.cid)
                    // category fill
                    if (category.size > 0) categoryControl.hint = category.elementAt(0).name
                    else categoryControl.hint = ""
                    // date fill
                    val dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
                    val actionDate: LocalDateTime = Instant.ofEpochMilli(todo.actionDate).atZone(ZoneOffset.UTC).toLocalDateTime()
                    btnTime.text = actionDate.format(timeFormat)
                    btnDate.text = actionDate.format(dateFormat)
                    // slider fill
                    sliderPriority.value = todo.priority.toFloat()
                    // message fill
                    message.setText(todo.message)

                } else {
                    topBar.title = "${user.username} ${resources.getString(R.string.todo_addtodo)}"

                }
                // time listener
                btn_time.setOnClickListener {
                    val timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()
                    timePicker.show(requireActivity().supportFragmentManager, "time_tag")
                    timePicker.addOnPositiveButtonClickListener {
                        timePickerHour = timePicker.hour
                        timePickerMinute = timePicker.minute
                        val times = (if (timePickerHour == 0) "00" else timePickerHour.toString()) + ":" + (if (timePickerMinute == 0) "00" else timePickerMinute.toString())
                        btn_time.text = times
                        // set time from current Todo
                        if (currentToDo != null) {
                            val selectedDate = Instant.ofEpochMilli(currentToDo!!.actionDate).atZone(ZoneOffset.UTC).toLocalDateTime()
                            val newActionDate = selectedDate.withHour(timePickerHour).withMinute(timePickerMinute)
                            val newInMillis: Long = (newActionDate.toEpochSecond(ZoneOffset.UTC) * 1000)
                            //actionDate = newInMillis
                            currentToDo!!.actionDate = newInMillis
                        }
                    }
                }
                // date listener
                btn_date.setOnClickListener {
                    val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
                    val datePicker: MaterialDatePicker<*> = builder.build()
                    datePicker.show(requireActivity().supportFragmentManager, datePicker.toString())
                    datePicker.addOnPositiveButtonClickListener {
                        // Log.e("date: ", datePicker.headerText)
                        btn_date.text = datePicker.headerText
                        val vals = datePicker.headerText.replace(",", "").split(" ") // Dec 12, 2020 -> Dec[0] 12[1] 2020[2]
                        val monthNames: MutableMap<String, Int> = HashMap()
                        monthNames["Jan"] = 1; monthNames["Feb"] = 2; monthNames["Mar"] = 3; monthNames["Apr"] = 4
                        monthNames["May"] = 5; monthNames["Jun"] = 6; monthNames["Jul"] = 7; monthNames["Aug"] = 8
                        monthNames["Sep"] = 9;
                        monthNames["Sept"] = 9; monthNames["Oct"] = 10; monthNames["Nov"] = 11; monthNames["Dec"] = 12
                        val curMonthNum = monthNames[vals[0]]
                        month = curMonthNum!!
                        year = vals[2].toInt()
                        day = vals[1].toInt()

                        val parseDate = "$curMonthNum/${vals[1]}/${vals[2]} $timePickerHour:$timePickerMinute"
                        if (year > 0 && month > 0 && day > 0) {
                            var formatter = DateTimeFormatter.ofPattern("M/d/y H:m")
                            val sf = SimpleDateFormat("M/d/y H:m")
                            sf.parse(parseDate)
                            val date: Date = sf.parse(parseDate)
                            actionDate = date.time
                            // set date from current Todo
                            if (currentToDo != null) {
                                var selectedDate = Instant.ofEpochMilli(actionDate).atZone(ZoneOffset.UTC).toLocalDateTime()
                                val newActionDate =
                                    Instant.ofEpochMilli(currentToDo!!.actionDate).atZone(ZoneOffset.UTC).toLocalDateTime().withYear(year).withMonth(month).withDayOfMonth(day)
                                val newInMillis: Long = (newActionDate.toEpochSecond(ZoneOffset.UTC) * 1000)
                                currentToDo!!.actionDate = newInMillis
                            }
                        }
                    }
                }
            }
        }
        btn_cancel.setOnClickListener {
            closeThisFragment()
        }

        // SAVE todo
        btn_save_todo.setOnClickListener {
            todoMessage = txt_msg.text.toString()
            if (currentToDo != null) {
                currentToDo!!.message = todoMessage
            }
            val newTodo = Todo(0, currentUser.uid, categoryId.toLong(), todoPriority, System.currentTimeMillis(), actionDate, todoMessage)
            if (year > 0 && month > 0 && day > 0 && timePickerHour >= 0 && timePickerMinute >= 0 && categoryId >= 0 && todoPriority >= 0 && todoMessage.isNotEmpty() || currentToDo != null) {
                // OK! All fields complete
                // save ToDo
                val db = TodoDatabase.getDatabase(requireContext())
                //Log.e("db: ", "created: ${db.toString()}")
                val dao = db.todoDao()
                if (currentToDo != null) dao.update(currentToDo!!)
                else dao.insert(newTodo)
                gotoWeekTasks(currentUser)
                Toast.makeText(context, getResources().getString(R.string.todo_save_complete), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, getResources().getString(R.string.todo_error_save), Toast.LENGTH_LONG).show()
                Log.e("newTodo: ", "$newTodo")
            }
        }


        btn_manageCategories.setOnClickListener {
            gotoCategories(currentUser)
        }
        // load drop-down list of categories
        val db = TodoDatabase.getDatabase(requireContext())
        val dao = db.todoCategoryDao()
        var categoryList = dao.loadCategories(currentUser.uid).toMutableList()
        for (item in categoryList) {
            Log.e("category: ", "$item")
        }
        val categoryAdapter = ArrayAdapter(requireContext(), R.layout.textview_item_categories, categoryList)
        val categoryListInput = view.findViewById<AutoCompleteTextView>(R.id.category_list)
        categoryListInput.setAdapter(categoryAdapter)
        categoryListInput.setOnItemClickListener { parent, view, position, id ->

            Log.e("catId: ", "id:$id position:$position text:${categoryListInput.text}")
            var cid = -1
            val selectedCategoryName = categoryListInput.text.toString()
            for ((index, category) in categoryList.withIndex()) {
                if (selectedCategoryName == category.name) {
                    categoryId = category.id
                    currentToDo?.cid = categoryId.toLong()
                }
            }
        }

        sliderPriority.addOnChangeListener { slider, value, fromUser ->
            todoPriority = value.toDouble()
            if (currentToDo != null) {
                currentToDo!!.priority = todoPriority
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

    private fun gotoCategories(user: UserModel) {
        val reqFragment = CategoriesFragment()
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

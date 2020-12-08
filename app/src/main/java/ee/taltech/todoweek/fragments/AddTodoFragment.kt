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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import kotlinx.android.synthetic.main.add_todo.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddTodoFragment : Fragment() {
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var timePickerHour: Int = -1
        var timePickerMinute: Int = -1
        var year: Int = -1
        var month: Int = -1
        var day: Int = -1
        var categoryId: Int = -1
        var todoPriority: Int = -1
        var todoMessage: String = ""


        if (arguments != null) {
            val user = arguments?.get("user")!! as UserModel
            currentUser = user
            topBar.title = "${user.username} ${resources.getString(R.string.todo_addtodo)}"
            // time listener

            btn_time.setOnClickListener {
                val timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()
                timePicker.show(requireActivity().supportFragmentManager, "time_tag")
                timePicker.addOnPositiveButtonClickListener {
                    timePickerHour = timePicker.hour
                    timePickerMinute = timePicker.minute
                    val times =
                        (if (timePickerHour == 0) "00" else timePickerHour.toString()) + ":" + (if (timePickerMinute == 0) "00" else timePickerMinute.toString())
                    btn_time.text = times
                }
            }
            // date listener
            btn_date.setOnClickListener {
                val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
                val datePicker: MaterialDatePicker<*> = builder.build()
                datePicker.show(requireActivity().supportFragmentManager, datePicker.toString())
                datePicker.addOnPositiveButtonClickListener {
                    Log.e("date: ", datePicker.headerText)
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
                    Log.e("parseDate: ", parseDate)
                    if (year > 0 && month > 0 && day > 0) {
                        var formatter = DateTimeFormatter.ofPattern("M/d/y H:m")
                        val sf = SimpleDateFormat("M/d/y H:m")
                        sf.parse(parseDate)
                        sf.calendar.time.hours = Log.e("date_val: ", sf.calendar.time.toString())
                        var actionDate = LocalDate.parse(parseDate, formatter)
                        Log.e("LocalDate: ", actionDate.toString())

                    }

                }

            }
        }
        btn_cancel.setOnClickListener {
            closeThisFragment()
        }
        btn_save_todo.setOnClickListener {
            if (year > 0 && month > 0 && day > 0 && timePickerHour >= 0 && timePickerMinute >= 0 && categoryId >= 0 && todoPriority >= 0 && todoMessage.isNotEmpty()) {

            } else {
                Toast.makeText(context, getResources().getString(R.string.todo_error_save), Toast.LENGTH_LONG).show()
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
        val txtCategory = view.findViewById<TextInputEditText>(R.id.txt_category_name)
//        val category = TodoCategory(0, currentUser.uid,txtCategory.text.toString())
//        dao.addCategory(category)
//        Log.e("addCategory: ", "added: $category")


        // val adapter = ArrayAdapter(requireContext(), R.layout.textview_item_categories, c)

        val catListInput = view.findViewById<AutoCompleteTextView>(R.id.category_list)
        //textView.threshold = 0
        catListInput.setAdapter(categoryAdapter)
        catListInput.setOnItemClickListener { parent, view, position, id ->

            Log.e("catId: ", "id:$id position:$position text:${catListInput.text}")
            categoryList = dao.loadCategories(currentUser.uid).toMutableList(); categoryAdapter.notifyDataSetChanged()

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

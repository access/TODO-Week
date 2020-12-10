package ee.taltech.todoweek.model

import android.content.Context
import android.os.Parcelable
import android.util.Log
import ee.taltech.todoweek.database.weekTaskList.Todo
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import kotlinx.android.parcel.Parcelize

data class WeekDay(
    val actionDate: Long, val userId: Long, val context: Context
) {
    private var dayTaskCount: Int = -1
    private var db: TodoDatabase = TodoDatabase.getDatabase(context)

    init {
        val dao = db.todoDao()
        dayTaskCount = dao.getToDosCount(actionDate, userId)
    }

    fun taskCount(): Int = dayTaskCount
    fun getNearTodo(): List<Todo> {
        val dao = db.todoDao()
        val result = arrayListOf(dao.getNearTodo(actionDate, userId))
        return result
    }
}
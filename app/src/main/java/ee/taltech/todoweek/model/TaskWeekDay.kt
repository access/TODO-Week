package ee.taltech.todoweek.model

import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase.Companion.getDatabase
import java.time.LocalDate

data class TaskWeekDay(
    val date: LocalDate,
    val taskCount: Int
) {
//    init {
//        val db = getDatabase()
//    }
}
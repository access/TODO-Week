package ee.taltech.todoweek.model

import ee.taltech.todoweek.database.weekTaskList.TodoCategory

interface CellClickListener {
    fun onCellClickListener(position: Int, category: TodoCategory)
}
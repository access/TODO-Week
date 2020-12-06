package ee.taltech.todoweek.database.weekTaskList

import androidx.room.*

@Entity(tableName = "todo_list")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "uid") val uid: Int = -1,
    @ColumnInfo(name = "priority") val priority: Int = -1,
    @ColumnInfo(name = "createDate") val createDate: Long = -1,
    @ColumnInfo(name = "actionDate") val actionDate: Long = -1,
    @ColumnInfo(name = "text") val message: String,
    @ColumnInfo(name = "done") val done: Boolean = false
) {
    override fun toString(): String {
        return "id:$id uid:$uid priority:$priority createDate:$createDate actionDate:$actionDate message:$message done:$done"
    }
}
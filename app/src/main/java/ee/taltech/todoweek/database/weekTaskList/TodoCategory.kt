package ee.taltech.todoweek.database.weekTaskList

import androidx.room.*

@Entity(tableName = "todo_category")
data class TodoCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "uid") val uid: Long = -1,
    @ColumnInfo(name = "name") val name: String,
) {
    override fun toString(): String {
        return "id:$id uid:$uid name:$name"
    }
}
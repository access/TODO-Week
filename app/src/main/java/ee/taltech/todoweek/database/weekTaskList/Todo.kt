package ee.taltech.todoweek.database.weekTaskList

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "todo_list")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "uid") val uid: Long = -1,
    @ColumnInfo(name = "cid") var cid: Long = -1,
    @ColumnInfo(name = "priority") var priority: Double = -1.0,
    @ColumnInfo(name = "createDate") val createDate: Long = -1,
    @ColumnInfo(name = "actionDate") var actionDate: Long = -1,
    @ColumnInfo(name = "text") var message: String,
    @ColumnInfo(name = "done") var done: Boolean = false
) : Parcelable {
    override fun toString(): String {
        return "id:$id uid:$uid cid:$cid priority:$priority createDate:$createDate actionDate:$actionDate message:$message done:$done"
    }
}
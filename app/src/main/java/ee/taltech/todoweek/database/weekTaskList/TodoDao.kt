package ee.taltech.todoweek.database.weekTaskList

import androidx.room.*
import java.util.List

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_list")
    fun getAll(): List<Todo>

    @Query("SELECT * FROM todo_list WHERE strftime('%d', datetime(:date, 'unixepoch', 'localtime')) = strftime('%d', datetime(actionDate, 'unixepoch', 'localtime')) and uid = :uid")
    fun getToDosByDate(date: Long, uid: Long): List<Todo>

    @Query("SELECT COUNT(*) FROM todo_list WHERE strftime('%d', datetime(:date, 'unixepoch', 'localtime')) = strftime('%d', datetime(actionDate, 'unixepoch', 'localtime')) and uid = :uid")
    fun getToDosCount(date: Long, uid: Long): Int

    @Query("SELECT * FROM todo_list WHERE strftime('%d', datetime(:date, 'unixepoch', 'localtime')) = strftime('%d', datetime(actionDate, 'unixepoch', 'localtime')) and uid = :uid ORDER BY createDate LIMIT 1")
    fun getNearTodo(date: Long, uid: Long): Todo

    @Query("DELETE FROM todo_list")
    fun clearTable()

    @Query("SELECT * FROM todo_list WHERE id=:id")
    fun loadSingleById(id: Int): Todo

    @Insert
    fun insert(todo: Todo)

    @Update
    fun update(todo: Todo)

    @Delete
    fun delete(todo: Todo)

}
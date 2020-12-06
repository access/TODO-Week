package ee.taltech.todoweek.database.weekTaskList

import androidx.room.*
import java.util.List

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_list")
    fun getAll(): List<Todo>

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
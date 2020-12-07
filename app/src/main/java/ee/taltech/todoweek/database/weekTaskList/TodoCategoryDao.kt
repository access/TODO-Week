package ee.taltech.todoweek.database.weekTaskList

import androidx.room.*
import java.util.List

@Dao
interface TodoCategoryDao {
    @Query("SELECT * FROM todo_category WHERE uid=:uid")
    fun loadCategories(uid: Long): List<TodoCategory>

    @Insert
    fun addCategory(category: TodoCategory)

    @Delete
    fun deleteCategory(category: TodoCategory)
}
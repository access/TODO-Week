package ee.taltech.todoweek

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.room.Room
import ee.taltech.todoweek.database.weekTaskList.Todo
import ee.taltech.todoweek.database.weekTaskList.TodoDao
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase
import ee.taltech.todoweek.database.weekTaskList.TodoDatabase.Companion.getDatabase
import ee.taltech.todoweek.fragments.AuthFragment
import java.sql.Timestamp
import java.time.LocalDate
import java.util.*

class MainActivity : AppCompatActivity(), NavigationHost {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionBar: ActionBar? = supportActionBar
        var first_run: Boolean = false
        actionBar?.hide()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.container, AuthFragment()).commit()
            first_run = true
            navigateTo(AuthFragment(), true)
            val db = getDatabase(this)
            //Log.e("db: ", "created: ${db.toString()}")
            val dao = db.todoDao()
            val t = Todo(0, 1,0, System.currentTimeMillis(), System.currentTimeMillis(),"hello",true)
            dao.insert(t)
            Log.e("db: ", "added: ${dao.toString()}")
            val all = dao.getAll()
            for (el in all) {
                Log.e("todo: ", "val: ${el.toString()}")
            }
        }

    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    override fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
        if (addToBackstack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("TODO-Start", "starting_session")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val myString = savedInstanceState.getString("update")
        //actionEvent.setText(myString)
    }
}
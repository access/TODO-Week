package ee.taltech.todoweek.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB
import kotlin.system.exitProcess

class AuthFragment : Fragment() {
    lateinit var userDB: UsersDB
    lateinit var settingsDB: SettingsDBHelper
    lateinit var currentUser: UserModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        settingsDB = SettingsDBHelper(this)
        userDB = UsersDB(this)

        val view = inflater.inflate(R.layout.auth, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.login_button).setOnClickListener {
            var uname = view.findViewById<TextInputEditText>(R.id.username_edit_text).text.toString()
            var pass = view.findViewById<TextInputEditText>(R.id.password_edit_text).text.toString()
            val isSavePass = if (view.findViewById<CheckBox>(R.id.chk_login_savepass).isChecked) 1 else 0


            for (x in userDB.readAllUsers()) {
                Log.e("user: ", x.toString())
            }
            Log.e("lastuser: ", settingsDB.getLastUsername().lastusername)

            if (isLoginDataValid(uname, pass, isSavePass)) {
                if (currentUser != null) {
                    gotoWeekTasks(currentUser)
                    if (currentUser.isSavedPassword != isSavePass) {
                        val isok = userDB.setUserSavePassState(currentUser.uid, isSavePass)
                    }
                    settingsDB.saveLastUsername(uname)
                }
            } else {
                Toast.makeText(context, getResources().getString(R.string.error_login), Toast.LENGTH_LONG).show()
            }
        }
        view.findViewById<MaterialButton>(R.id.cancel_button).setOnClickListener {
            exitProcess(0)
        }
        view.findViewById<BottomNavigationItemView>(R.id.create).setOnClickListener {
            navigateTo(CreateUserFragment(), true)
        }
        // last loaded user - if saved pass load week tasks
        val isExistsUser = userDB.isUsernameExists(settingsDB.getLastUsername().lastusername)
        if (isExistsUser) {
            val lastUser = userDB.readUser(settingsDB.getLastUsername().lastusername)
            if (lastUser.uid >= 0) {
                currentUser = lastUser
                if (lastUser.isSavedPassword > 0) {
                    val usernameInput = view.findViewById<TextInputEditText>(R.id.username_edit_text)
                    val passwordInput = view.findViewById<TextInputEditText>(R.id.password_edit_text)
                    usernameInput.setText(currentUser.username)
                    passwordInput.setText(currentUser.password)
                                       gotoWeekTasks(currentUser)
                }
            }
        }

    }

    private fun isLoginDataValid(username: String, password: String, checkedSavePass: Int): Boolean {
        if (username.isEmpty()) return false
        userDB = UsersDB(this)
        val isExistsUser = userDB.isUsernameExists(username)
        if (isExistsUser) {
            val user = userDB.readUser(username)
            if (user.uid >= 0) {
                currentUser = user
                //Toast.makeText(context, "user: $username pass: ${user.password} uid: ${user.uid} isSavedPass:${user.isSavedPassword}", Toast.LENGTH_LONG).show()
                return if (user.isSavedPassword > 0 && checkedSavePass > 0) {
                    true
                } else user.password == password
            }
        }
        return false
    }

    fun gotoWeekTasks(user: UserModel) {
        val weekFrag = WeekListFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", user)
        weekFrag.setArguments(bundle)
        navigateTo(weekFrag, true)
    }

    private fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = fragmentManager?.beginTransaction()?.replace(R.id.container, fragment)
        if (addToBackstack) {
            transaction?.addToBackStack(null)
        }
        transaction?.commit()
    }
}

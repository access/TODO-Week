package ee.taltech.todoweek.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import ee.taltech.todoweek.NavigationHost
import ee.taltech.todoweek.R
import ee.taltech.todoweek.database.settings.SettingsDBHelper
import ee.taltech.todoweek.database.user.UserModel
import ee.taltech.todoweek.database.user.UsersDB


class CreateUserFragment : Fragment(), NavigationHost {
    lateinit var userDB: UsersDB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        userDB = UsersDB(this)

        val view = inflater.inflate(R.layout.create_user, container, false)

        // Set an error if the password is less than 8 characters.
//        view.findViewById<MaterialButton>(R.id.createUser_button).setOnClickListener {
//            Log.e("createUser_button: ","ok")
//        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //userDB = UsersDB(this)

        view.findViewById<MaterialButton>(R.id.createUser_button).setOnClickListener {
            val uname = view.findViewById<TextInputEditText>(R.id.username_edit_text).text.toString()
            val pass = view.findViewById<TextInputEditText>(R.id.password_edit_text).text.toString()
            val isSavePass = if (view.findViewById<CheckBox>(R.id.chk_savepass).isChecked) 1 else 0
            if (isRegistrationValid(uname, pass, isSavePass)) {
                val newUserId = userDB.insertUser(UserModel(0, isSavePass, uname, pass))
                if (newUserId >= 0) {
                    Toast.makeText(context, getResources().getString(R.string.reg_complete), Toast.LENGTH_LONG).show()
                    val settingsDB = SettingsDBHelper(this)
                    settingsDB.saveLastUsername(uname)
                    val user = UserModel(newUserId, isSavePass, uname, pass)
                    gotoWeekTasks(user)
                    if (isSavePass > 0) settingsDB.saveLastUsername(uname)
                    else settingsDB.saveLastUsername("none")

                }
            } else {
                Toast.makeText(context, getResources().getString(R.string.error_registration), Toast.LENGTH_LONG).show()
            }
        }

        view.findViewById<MaterialButton>(R.id.cancel_button).setOnClickListener {
            for (x in userDB.readAllUsers()) {
                userDB.deleteUser(x.uid.toString())
                Log.e("deleted user: ", x.toString())
            }
            navigateTo(AuthFragment(), true)
        }
    }


    // "isPasswordValid" from "Navigate to the next Fragment" section method goes here
    private fun isRegistrationValid(
        username: String, password: String, isSavePass: Int
    ): Boolean {
        val settingsDB = SettingsDBHelper(this)
        //check if username already exists
        var userAlreadyExists = userDB.isUsernameExists(username)
        if (userAlreadyExists) {
            Toast.makeText(context, getResources().getString(R.string.username_exists), Toast.LENGTH_LONG).show()
            Log.e("userAlreadyExists: ", userAlreadyExists.toString())
            return false
        }
        var showresult = userDB.readAllUsers()
        Log.e("ulen: ", "${showresult.size}")

        for (x in userDB.readAllUsers()) {
            Log.e("user: ", x.toString())
            //usersDBHelper.deleteUser(x.uid.toString())
        }
        Log.e("lastuser: ", settingsDB.getLastUsername().lastusername)


        if (username.isEmpty()) return false
        // pass no need check, may be empty - optional
        return true
    }

    fun gotoWeekTasks(user: UserModel) {
        val weekFrag = WeekListFragment()
        val bundle = Bundle()
        bundle.putParcelable("user", user)
        weekFrag.setArguments(bundle)
        navigateTo(weekFrag, true)
    }

    override fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = fragmentManager?.beginTransaction()?.replace(R.id.container, fragment)

        if (addToBackstack) {
            transaction?.addToBackStack(null)
        }

        transaction?.commit()
    }


}

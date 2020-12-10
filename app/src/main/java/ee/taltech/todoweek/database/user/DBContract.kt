package ee.taltech.todoweek.database.user

import android.provider.BaseColumns

object DBContract {

    class UserEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "users"
            val COLUMN_USER_ID = "uid"
            val COLUMN_USERNAME = "username"
            val COLUMN_PASSWORD = "password"
            val COLUMN_IS_PASSWORD_SAVED = "is_pass_saved"
        }
    }
}
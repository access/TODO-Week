package ee.taltech.todoweek.database.settings

import android.provider.BaseColumns

object DBSettingsContract {

    /* Inner class that defines the table contents */
    class SettingsEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "settings"
            val SETTINGS_ID = "sid"
            val COLUMN_LAST_USERNAME = "lastusername"
//            val COLUMN_USERNAME = "username"
//            val COLUMN_PASSWORD = "password"
//            val COLUMN_IS_PASSWORD_SAVED = "is_pass_saved"
        }
    }
}
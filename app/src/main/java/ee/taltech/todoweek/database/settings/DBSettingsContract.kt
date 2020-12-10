package ee.taltech.todoweek.database.settings

import android.provider.BaseColumns

object DBSettingsContract {
    class SettingsEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "settings"
            val SETTINGS_ID = "sid"
            val COLUMN_LAST_USERNAME = "lastusername"
        }
    }
}
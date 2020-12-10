package ee.taltech.todoweek.database.settings

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.fragment.app.Fragment

class SettingsDBHelper(context: Fragment) : SQLiteOpenHelper(context.requireContext(), DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun saveLastUsername(username: String): Boolean {
        val db = writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBSettingsContract.SettingsEntry.COLUMN_LAST_USERNAME, username)
        // Insert the new row, returning the primary key value of the new row
        val updateRowID = db.update(DBSettingsContract.SettingsEntry.TABLE_NAME, values, DBSettingsContract.SettingsEntry.SETTINGS_ID + " = 1", null)
        if (updateRowID == 0) {
            db.insert(DBSettingsContract.SettingsEntry.TABLE_NAME, null, values)
        }
        return updateRowID == 1
    }

    @Throws(SQLiteConstraintException::class)
    fun getLastUsername(): SettingsModel {
        var settings = SettingsModel("none")
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                "select * from " + DBSettingsContract.SettingsEntry.TABLE_NAME + " WHERE " + DBSettingsContract.SettingsEntry.SETTINGS_ID + " = 1", null
            )
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            db.execSQL(SQL_SET_DEFAULTS)
        }
        var lastUsername: String
        if (cursor != null && cursor.moveToFirst()) {
            lastUsername = cursor.getString(cursor.getColumnIndex(DBSettingsContract.SettingsEntry.COLUMN_LAST_USERNAME))
            settings = SettingsModel(lastUsername)
        }
        return settings
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "TODO-Week.db"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + DBSettingsContract.SettingsEntry.TABLE_NAME + " (" + DBSettingsContract.SettingsEntry.SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBSettingsContract.SettingsEntry.COLUMN_LAST_USERNAME + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBSettingsContract.SettingsEntry.TABLE_NAME
        private val SQL_SET_DEFAULTS =
            "INSERT INTO " + DBSettingsContract.SettingsEntry.TABLE_NAME + " (" + DBSettingsContract.SettingsEntry.COLUMN_LAST_USERNAME + ") VALUES ('none')"
    }
}
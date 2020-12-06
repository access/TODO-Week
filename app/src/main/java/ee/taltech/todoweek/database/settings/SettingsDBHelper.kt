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

    //
    @Throws(SQLiteConstraintException::class)
    fun saveSetting(setting: SettingsModel): Boolean {
        // if (setting.lastusername.isEmpty()) return false
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBSettingsContract.SettingsEntry.COLUMN_LAST_USERNAME, setting.lastusername)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBSettingsContract.SettingsEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun saveLastUsername(username: String): Boolean {
        val db = writableDatabase
//        db.execSQL(SQL_CREATE_ENTRIES)

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBSettingsContract.SettingsEntry.COLUMN_LAST_USERNAME, username)

        // Insert the new row, returning the primary key value of the new row
        val updateRowID =
            db.update(DBSettingsContract.SettingsEntry.TABLE_NAME, values, DBSettingsContract.SettingsEntry.SETTINGS_ID + " = 1",null)
     //   Log.e("saveLastUsername: ", "newRowId: $updateRowID")
//        val updateRowID = db.update(DBSettingsContract.SettingsEntry.TABLE_NAME, null,values )
       // db.execSQL(SQL_DELETE_ENTRIES)
        return updateRowID == 1
    }

    //
//    @Throws(SQLiteConstraintException::class)
//    fun deleteUser(userid: String): Boolean {
//        // Gets the data repository in write mode
//        val db = writableDatabase
//
//        // Define 'where' part of query.
//        val selection = DBContract.UserEntry.COLUMN_USER_ID + " LIKE ?"
//        // Specify arguments in placeholder order.
//        val selectionArgs = arrayOf(userid)
//        // Issue SQL statement.
//        db.delete(DBContract.UserEntry.TABLE_NAME, selection, selectionArgs)
//
//        return true
//    }
//
//    fun readUser(userid: String): ArrayList<UserModel> {
//        val users = ArrayList<UserModel>()
//        val db = writableDatabase
//
//        db.execSQL(SQL_DELETE_ENTRIES)
//        db.execSQL(SQL_CREATE_ENTRIES)
//
//
//        var cursor: Cursor? = null
//        try {
//            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_NAME + " WHERE " + DBContract.UserEntry.COLUMN_USER_ID + "='" + userid + "'", null)
//        } catch (e: SQLiteException) {
//            // if table not yet present, create it
//            db.execSQL(SQL_CREATE_ENTRIES)
//            return ArrayList()
//        }
//
//        var uid: Int
//        var isSavedPass: Int
//        var username: String
//        var password: String
//        if (cursor!!.moveToFirst()) {
//            while (cursor.isAfterLast == false) {
//                uid = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_ID))
//                isSavedPass = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_IS_PASSWORD_SAVED))
//                username = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USERNAME))
//                password = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_PASSWORD))
//
//                users.add(UserModel(uid, isSavedPass, username, password))
//                cursor.moveToNext()
//            }
//        }
//        return users
//    }
//
    @Throws(SQLiteConstraintException::class)
    fun getLastUsername(): SettingsModel {
        var settings = SettingsModel("none")
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBSettingsContract.SettingsEntry.TABLE_NAME+" WHERE "+DBSettingsContract.SettingsEntry.SETTINGS_ID+" = 1", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            db.execSQL(SQL_SET_DEFAULTS)
            //return settings
            //Log.e("SQLiteExceptionzzz::: ", e.message.toString())
        }
 //       Log.e("cursor::: ", cursor?.count.toString())

 //       db.execSQL(SQL_DELETE_ENTRIES)
//        db.execSQL(SQL_CREATE_ENTRIES)

        var lastUsername: String
        if (cursor!=null && cursor.moveToFirst()) {
            //while (!cursor.isAfterLast) {
            lastUsername = cursor.getString(cursor.getColumnIndex(DBSettingsContract.SettingsEntry.COLUMN_LAST_USERNAME))
     //       Log.e("LUN::: ", lastUsername)
            settings = SettingsModel(lastUsername)
            //cursor.moveToNext()
            //}
        }
        return settings
    }
//
//    fun isUsernameExists(username: String): Boolean {
//        if (username.isEmpty()) return false
//        val users = ArrayList<UserModel>()
//        val db = writableDatabase
//        var cursor: Cursor? = null
//        try {
//            cursor = db.rawQuery("select COUNT(*) AS cnt from " + DBContract.UserEntry.TABLE_NAME + " where UPPER(" + DBContract.UserEntry.COLUMN_USERNAME + ") = UPPER('" + username + "')", null)
//        } catch (e: SQLiteException) {
//            return false
//        }
//        if (cursor == null) return false
//        val count = DatabaseUtils.queryNumEntries(
//            db, "users", "UPPER(username)=UPPER('$username')"
//        )
//        Log.e("count: ", count.toString())
//        return count > 0L
//    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "TODO-Week.db"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + DBSettingsContract.SettingsEntry.TABLE_NAME + " (" + DBSettingsContract.SettingsEntry.SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBSettingsContract.SettingsEntry.COLUMN_LAST_USERNAME + " TEXT)"
        // +
//                    DBContract.UserEntry.COLUMN_IS_PASSWORD_SAVED + " INTEGER," +
//                    DBContract.UserEntry.COLUMN_PASSWORD + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBSettingsContract.SettingsEntry.TABLE_NAME
        private val SQL_SET_DEFAULTS =
            "INSERT INTO " + DBSettingsContract.SettingsEntry.TABLE_NAME + " (" + DBSettingsContract.SettingsEntry.COLUMN_LAST_USERNAME + ") VALUES ('none')"
    }

}
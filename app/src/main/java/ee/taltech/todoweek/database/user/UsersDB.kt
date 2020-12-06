package ee.taltech.todoweek.database.user

import android.content.ContentValues
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import androidx.fragment.app.Fragment
import java.util.*


class UsersDB(context: Fragment) : SQLiteOpenHelper(context.requireContext(), DATABASE_NAME, null, DATABASE_VERSION) {
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
    fun insertUser(user: UserModel): Long {
        if (user.username.isEmpty()) return -1
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_IS_PASSWORD_SAVED, user.isSavedPassword)
        values.put(DBContract.UserEntry.COLUMN_USERNAME, user.username)
        values.put(DBContract.UserEntry.COLUMN_PASSWORD, user.password)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.UserEntry.TABLE_NAME, null, values)

        return newRowId
    }

    @Throws(SQLiteConstraintException::class)
    fun setUserSavePassState(uid: Long, state: Int): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // update user save pass state
//        var cursor: Cursor? = null
//        try {
//            cursor = db.rawQuery(
//                "UPDATE " + DBContract.UserEntry.TABLE_NAME + " SET " + DBContract.UserEntry.COLUMN_IS_PASSWORD_SAVED + "='" + state + "' WHERE " + DBContract.UserEntry.COLUMN_USER_ID + "='" + uid + "'",
//                null
//            )
//        } catch (e: SQLiteException) {
//            return false
//        }
//        return cursor != null
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_IS_PASSWORD_SAVED, state)
        // Update the row, returning the primary key value of the row
        val newRowId = db.update(DBContract.UserEntry.TABLE_NAME, values, DBContract.UserEntry.COLUMN_USER_ID + "=$uid", null)
        return newRowId >= 0
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteUser(userid: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Define 'where' part of query.
        val selection = DBContract.UserEntry.COLUMN_USER_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(userid)
        // Issue SQL statement.
        db.delete(DBContract.UserEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    fun readUser(username: String): UserModel {
        var user = UserModel(-1, -1, "", "")
        val db = writableDatabase
//        db.execSQL(SQL_DELETE_ENTRIES)
//        db.execSQL(SQL_CREATE_ENTRIES)
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                "select * from " + DBContract.UserEntry.TABLE_NAME + " WHERE UPPER(" + DBContract.UserEntry.COLUMN_USERNAME + ")=UPPER('" + username + "')",
                null
            )
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return user
        }

        var uid: Long
        var isSavedPass: Int
        var username: String
        var password: String
        if (cursor!!.moveToFirst()) {
            uid = cursor.getLong(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_ID))
            isSavedPass = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_IS_PASSWORD_SAVED))
            username = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USERNAME))
            password = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_PASSWORD))

            user = UserModel(uid, isSavedPass, username, password)
        }
        return user
    }

    fun readAllUsers(): ArrayList<UserModel> {
        val users = ArrayList<UserModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.UserEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

//        db.execSQL(SQL_DELETE_ENTRIES)
//        db.execSQL(SQL_CREATE_ENTRIES)

        var uid: Long
        var isSavedPass: Int
        var username: String
        var password: String
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                uid = cursor.getLong(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USER_ID))
                isSavedPass = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_IS_PASSWORD_SAVED))
                username = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_USERNAME))
                password = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_PASSWORD))

                users.add(UserModel(uid, isSavedPass, username, password))
                cursor.moveToNext()
            }
        }
        return users
    }

    fun isUsernameExists(username: String): Boolean {
        if (username.isEmpty()) return false
        val users = ArrayList<UserModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(
                "select COUNT(*) AS cnt from " + DBContract.UserEntry.TABLE_NAME + " where UPPER(" + DBContract.UserEntry.COLUMN_USERNAME + ") = UPPER('" + username + "')",
                null
            )
        } catch (e: SQLiteException) {
            return false
        }
        if (cursor == null) return false
        val count = DatabaseUtils.queryNumEntries(
            db, "users", "UPPER(username)=UPPER('$username')"
        )
        //Log.e("count: ", count.toString())
        return count > 0L
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "TODO-Week.db"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_NAME + " (" + DBContract.UserEntry.COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DBContract.UserEntry.COLUMN_USERNAME + " TEXT," + DBContract.UserEntry.COLUMN_IS_PASSWORD_SAVED + " INTEGER," + DBContract.UserEntry.COLUMN_PASSWORD + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_NAME
    }

}
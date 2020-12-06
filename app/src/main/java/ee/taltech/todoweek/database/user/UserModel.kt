package ee.taltech.todoweek.database.user

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UserModel(
    val uid: Long, var isSavedPassword: Int, val username: String, val password: String
) : Parcelable{
    override fun toString(): String {
        return "uid:$uid username:$username password:$password isSavedPassword:$isSavedPassword"
    }
}

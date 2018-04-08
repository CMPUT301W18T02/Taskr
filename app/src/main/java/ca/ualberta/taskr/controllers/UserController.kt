package ca.ualberta.taskr.controllers

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import ca.ualberta.taskr.R
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit

/**
 * UserController class. This class is responsible for passing information to and From
 * [SharedPreferences] for storage of user information between activities and between
 * use sessions
 *
 * @see [SharedPreferences]
 */
class UserController(val context: Context) {

    /**
     * Set the locally stored username in shared prefs
     *
     * @param username The username to be saved
     */
    fun setLocalUsername(username: String) {
        val editor: SharedPreferences.Editor = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        ).edit()
        editor.putString(context.getString(R.string.sf_username_key), username)
        editor.apply()
    }

    /**
     * Get the locally stored username in shared prefs
     *
     * @return The username or "" if not set
     */
    fun getLocalUserName(): String {
        val sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        )
        return sharedPrefs.getString(context.getString(R.string.sf_username_key), "")
    }

    /**
     * Set the locally stored user in shared prefs
     *
     * @param user The user to be saved or null to remove the user object
     */
    fun setLocalUserObject(user: User?){
        val editor: SharedPreferences.Editor = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        ).edit()
        if (user == null){
            editor.remove(context.getString(R.string.prefs_user))
        }
        else{
            val gsonUser = GenerateRetrofit.generateGson().toJson(user, User::class.java)
            editor.putString(context.getString(R.string.prefs_user), gsonUser)
        }
        editor.apply()
    }

    /**
     * Get the locally stored user in shared prefs
     *
     * @return The user object or null if not set
     */
    fun getLocalUserObject() : User? {
        val sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        )
        val userString = sharedPrefs.getString(context.getString(R.string.prefs_user), null) ?: return null
        return GenerateRetrofit.generateGson().fromJson(userString, User::class.java)
    }

}
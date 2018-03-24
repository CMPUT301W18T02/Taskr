package ca.ualberta.taskr.models.elasticsearch

import android.os.AsyncTask
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.User
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.util.Log
import ca.ualberta.taskr.R
import com.google.gson.reflect.TypeToken
import java.io.IOError


/**
 *  3/22/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

class CachingRetrofit(val context: Context) {
    private val server = GenerateRetrofit.generateRetrofit()
    private val gson = GenerateRetrofit.generateGson()


    private fun setUsers(users: List<User>) {
        val editor: SharedPreferences.Editor = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        ).edit()
        editor.putString(context.getString(R.string.sf_users_key), gson.toJson(users))
        editor.apply()
    }

    private fun getUsersFromDisk(): List<User> {
        val userListType = object : TypeToken<List<@JvmSuppressWildcards User>>() {}.type

        val sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        )
        return gson.fromJson(sharedPrefs
                .getString(context.getString(R.string.sf_users_key), "[]"), userListType)
    }

    private fun setTasks(tasks: List<Task>) {
        val editor: SharedPreferences.Editor = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        ).edit()
        editor.putString(context.getString(R.string.sf_tasks_key), gson.toJson(tasks))
        editor.apply()
    }

    private fun getTasksFromDisk(): List<Task> {
        val taskListType = object : TypeToken<List<@JvmSuppressWildcards Task>>() {}.type

        val sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        )
        return gson.fromJson(sharedPrefs
                .getString(context.getString(R.string.sf_tasks_key), "[]"), taskListType)

    }

    private fun getLocalUserName(): String {
        val sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        )
        return sharedPrefs.getString(context.getString(R.string.sf_username_key), "")
    }

    private fun addTaskToUpload(pair: Pair<Task, Task>) {
        val currentTasksToUpload: ArrayList<Pair<Task, Task>> = getTasksToUpload()

        currentTasksToUpload.add(pair)

        val editor: SharedPreferences.Editor = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        ).edit()
        editor.putString(context.getString(R.string.sf_tasks_to_upload_key), gson.toJson(currentTasksToUpload))
        editor.apply()
    }

    private fun getTasksToUpload(): ArrayList<Pair<Task, Task>> {
        val taskListType = object : TypeToken<List<@JvmSuppressWildcards Pair<Task, Task>>>() {}.type

        val sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        )
        return gson.fromJson(sharedPrefs
                .getString(context.getString(R.string.sf_tasks_to_upload_key), "[]"), taskListType)

    }


    private fun notifyUserOfBids(tasks: List<Task>) {
        val owner = getLocalUserName()
        val oldTasks = getTasksFromDisk().filter({ it -> it.owner == owner })
        for (task in tasks.filter({ it -> it.owner == owner })) {

        }
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    inner class getServerInfo(val callback: Callback<ServerInfo?>) : AsyncTask<Void, Void, ServerInfo?>() {
        private var resultFromCache: Boolean = false
        override fun onPostExecute(result: ServerInfo?) {
            super.onPostExecute(result)
            if (result == null) {
                callback.onFailure()
            } else {
                callback.onResponse(result, resultFromCache)
            }

        }

        override fun doInBackground(vararg params: Void): ServerInfo? {
            return server.getServerInfo().execute().body()
        }
    }

    inner class getUsers(val callback: Callback<List<User>>) : AsyncTask<Void, Void, List<User>>() {
        private var resultFromCache: Boolean = false
        override fun onPostExecute(result: List<User>) {
            super.onPostExecute(result)
            callback.onResponse(result, resultFromCache)

        }

        override fun doInBackground(vararg params: Void): List<User> {
            var users = server.getUsers().execute().body()
            if (users == null) {
                resultFromCache = true
                users = getUsersFromDisk()
            } else {
                setUsers(users)
            }

            return users
        }
    }

    inner class getTasks(val callback: Callback<List<Task>>) : AsyncTask<Void, Void, List<Task>>() {
        private var resultFromCache: Boolean = false
        override fun onPostExecute(result: List<Task>) {
            super.onPostExecute(result)
            callback.onResponse(result, resultFromCache)

        }

        override fun doInBackground(vararg params: Void): List<Task> {
            var tasks: List<Task>
            try {
                val t = server.getTasks().execute().body()
                if (t == null) {
                    resultFromCache = true
                    tasks = getTasksFromDisk()
                } else {
                    tasks = t
                    setTasks(tasks)
//                notifyUserOfBids(tasks)
                }
            } catch (e: Throwable) {
                resultFromCache = true
                tasks = getTasksFromDisk()

            }


            println(tasks)
            return tasks
        }
    }

    inner class updateTask(val callback: Callback<Boolean>) : AsyncTask<Pair<Task, Task>, Void, Boolean>() {
        override fun onPostExecute(uploadSucceeded: Boolean) {
            super.onPostExecute(uploadSucceeded)
            if (uploadSucceeded) {
                callback.onResponse(uploadSucceeded, false)
            } else {
                callback.onFailure()
            }
        }

        override fun doInBackground(vararg params: Pair<Task, Task>): Boolean {
            var uploadSuccessful = true
            for (pair in params) {

                try {
                    val (new, old) = pair
                    val query = Query.taskQuery(old.owner, old.title, old.description)
                    val id = server.getTaskID(query).execute().body()
                    if (id == null) {
                        addTaskToUpload(pair)
                        throw IOError(Throwable())
                    } else {
                        server.updateTask(id._id, new).execute()
                    }
                } catch (e: IOError) {
                    Log.i("Network", "Unavailable")
                    uploadSuccessful = false
                }
            }
            return uploadSuccessful
        }
    }


    fun updateUser(id: String, user: User): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun createTask(task: Task): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun createUser(user: User): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun isOnline(): Boolean {
        TODO("not implemented")
    }


}
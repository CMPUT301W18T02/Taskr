package ca.ualberta.taskr.models.elasticsearch

import android.os.AsyncTask
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.User
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.util.Log
import ca.ualberta.taskr.R
import com.google.common.io.Files
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOError


/**
 *  3/22/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */
//TODO Documentation
//TODO UPLOAD CACHED TASKS and USERS
class CachingRetrofit(val context: Context) {
    private val server = GenerateRetrofit.generateRetrofit()
    private val gson = Gson()


    private fun setUsers(users: List<User>) {
        val file = File(context.filesDir, "users.json")
        Files.asCharSink(file, Charsets.UTF_8).write(gson.toJson(users))
    }

    private fun getUsersFromDisk(): List<User> {
        val file = File(context.filesDir, "users.json")
        val json = Files.asCharSource(file, Charsets.UTF_8).read()
        val taskListType = object : TypeToken<List<@JvmSuppressWildcards User>>() {}.type
        return gson.fromJson(json, taskListType)
    }

    private fun setTasks(tasks: List<Task>) {
        val file = File(context.filesDir, "tasks.json")
        Files.asCharSink(file, Charsets.UTF_8).write(gson.toJson(tasks))
    }

    private fun getTasksFromDisk(): List<Task> {
        // https://stackoverflow.com/questions/14376807/how-to-read-write-string-from-a-file-in-android
        val file = File(context.filesDir, "tasks.json")
        val json = Files.asCharSource(file, Charsets.UTF_8).read()

        val taskListType = object : TypeToken<List<@JvmSuppressWildcards Task>>() {}.type

        return gson.fromJson(json, taskListType)
    }

    private fun getLocalUserName(): String {
        val sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        )
        return sharedPrefs.getString(context.getString(R.string.sf_username_key), "")
    }

    private fun addTaskToUpload(pair: Pair<Task?, Task>) {
        val file = File(context.filesDir, "tasksToUpload.json")
        val currentTasksToUpload = getTasksToUpload()
        currentTasksToUpload.add(pair)
        Files.asCharSink(file, Charsets.UTF_8).write(gson.toJson(currentTasksToUpload))
    }

    private fun getTasksToUpload(): ArrayList<Pair<Task?, Task>> {
        val file = File(context.filesDir, "tasksToUpload.json")
        if (!file.exists()) {
            file.createNewFile()
            Files.asCharSink(file, Charsets.UTF_8).write("[]")
        }

        val json = Files.asCharSource(file, Charsets.UTF_8).read()

        val tasksToUploadListType = object : TypeToken<List<@JvmSuppressWildcards Pair<Task?, Task>>>() {}.type

        return gson.fromJson(json, tasksToUploadListType)

    }

    private fun addUserToUpload(pair: User) {
        val currentUsersToUpload = getUsersToUpload()
        currentUsersToUpload.add(pair)
        val file = File(context.filesDir, "usersToUpload.json")
        Files.asCharSink(file, Charsets.UTF_8).write(gson.toJson(currentUsersToUpload))
    }

    private fun getUsersToUpload(): ArrayList<User> {
        val file = File(context.filesDir, "usersToUpload.json")
        if (!file.exists()) {
            file.createNewFile()
            Files.asCharSink(file, Charsets.UTF_8).write("[]")
        }

        val json = Files.asCharSource(file, Charsets.UTF_8).read()

        val usersToUploadListType = object : TypeToken<List<@JvmSuppressWildcards User>>() {}.type

        return gson.fromJson(json, usersToUploadListType)

    }


    private fun notifyUserOfBids(tasks: List<Task>) {
        val owner = getLocalUserName()
        val oldTasks = getTasksFromDisk().filter({ it -> it.owner == owner })
        for (task in tasks.filter({ it -> it.owner == owner })) {

        }
        TODO("not implemented")
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

    inner class updateTask(val callback: Callback<Boolean>) : AsyncTask<Pair<Task?, Task>, Void, Boolean>() {
        override fun onPostExecute(uploadSucceeded: Boolean) {
            super.onPostExecute(uploadSucceeded)
            if (uploadSucceeded) {
                callback.onResponse(uploadSucceeded, uploadSucceeded)
            } else {
                callback.onFailure()
            }
        }

        override fun doInBackground(vararg params: Pair<Task?, Task>): Boolean {
            var uploadSuccessful = true
            for (pair in params) {

                try {
                    val (old, new) = pair
                    if (old == null) {
                        try {
                            server.createTask(new)
                        } catch (e: Throwable) {
                            addTaskToUpload(pair)
                        }
                    } else {
                        val query = Query.taskQuery(old.owner, old.title, old.description)
                        val id = server.getTaskID(query).execute().body()
                        if (id == null) {
                            addTaskToUpload(pair)
                            throw IOError(Throwable())
                        } else {
                            server.updateTask(id._id, new).execute()
                        }
                    }
                } catch (e: Throwable) {
                    Log.i("Network", "Unavailable")
                    uploadSuccessful = false
                }
            }
            return uploadSuccessful
        }
    }


    inner class updateUser(val callback: Callback<Boolean>) : AsyncTask<User, Void, Boolean>() {
        override fun onPostExecute(uploadSucceeded: Boolean) {
            super.onPostExecute(uploadSucceeded)
            if (uploadSucceeded) {
                callback.onResponse(uploadSucceeded, uploadSucceeded)
            } else {
                callback.onFailure()
            }
        }

        override fun doInBackground(vararg params: User): Boolean {
            var uploadSuccessful = true
            for (user in params) {

                try {
                    val query = Query.userQuery(user.username)
                    val id = server.getUserID(query).execute().body()
                    if (id == null) {
                        addUserToUpload(user)
                        throw IOError(Throwable())
                    } else {
                        server.updateUser(id._id, user).execute()
                    }

                } catch (e: Throwable) {
                    Log.i("Network", "Unavailable")
                    uploadSuccessful = false
                }
            }
            return uploadSuccessful
        }
    }

    fun isOnline(): Boolean {
        TODO("not implemented")
    }


}
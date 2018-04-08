package ca.ualberta.taskr.models.elasticsearch

import android.app.NotificationManager
import android.os.AsyncTask
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.User
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import ca.ualberta.taskr.R
import com.google.common.io.Files
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOError
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Build.VERSION
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.support.annotation.RequiresApi
import ca.ualberta.taskr.ViewTaskActivity


/**
 *  3/22/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

// TODO: Documentation
// TODO: UPLOAD CACHED TASKS and USERS

/**
 * CachingRetrofit class. This class is responsible for fetching cached data and locally mocking
 * Retrofit calls instead of trying to grab results via elasticsearch
 */
class CachingRetrofit(val context: Context) {
    private val server = GenerateRetrofit.generateRetrofit()
    private val gson = Gson()

    /**
     * Save users to disk
     * @param users a [List] of [User] objects to save to disk
     */
    private fun setUsers(users: List<User>) {
        val file = File(context.filesDir, "users.json")
        if (!file.exists()) {
            file.createNewFile()
            Files.asCharSink(file, Charsets.UTF_8).write("[]")
        }

        Files.asCharSink(file, Charsets.UTF_8).write(gson.toJson(users))
    }

    /**
     * Get users from disk
     * @return a [List] of [User] objects
     */
    private fun getUsersFromDisk(): List<User> {
        val file = File(context.filesDir, "users.json")
        if (!file.exists()) {
            file.createNewFile()
            Files.asCharSink(file, Charsets.UTF_8).write("[]")
        }

        val json = Files.asCharSource(file, Charsets.UTF_8).read()
        val taskListType = object : TypeToken<List<@JvmSuppressWildcards User>>() {}.type
        return gson.fromJson(json, taskListType)
    }

    /**
     * Save tasks to disk
     * @param tasks a [List] of [Task] objects
     */
    private fun setTasks(tasks: List<Task>) {
        val file = File(context.filesDir, "tasks.json")
        if (!file.exists()) {
            file.createNewFile()
            Files.asCharSink(file, Charsets.UTF_8).write("[]")
        }

        Files.asCharSink(file, Charsets.UTF_8).write(gson.toJson(tasks))
    }

    /**
     * Load tasks from disk
     * @return a [List] of [Task] objects
     */
    private fun getTasksFromDisk(): List<Task> {
        // https://stackoverflow.com/questions/14376807/how-to-read-write-string-from-a-file-in-android
        val file = File(context.filesDir, "tasks.json")
        if (!file.exists()) {
            file.createNewFile()
            Files.asCharSink(file, Charsets.UTF_8).write("[]")
        }

        val json = Files.asCharSource(file, Charsets.UTF_8).read()

        val taskListType = object : TypeToken<List<@JvmSuppressWildcards Task>>() {}.type

        return gson.fromJson(json, taskListType)
    }

    // TODO: This should be done using the UserController class
    /**
     * Get the application user's username
     * @returns a [String] containing the username
     */
    private fun getLocalUserName(): String {
        val sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.prefs_name),
                AppCompatActivity.MODE_PRIVATE
        )
        return sharedPrefs.getString(context.getString(R.string.sf_username_key), "")
    }

    /**
     * Create a list of tasks that will be uploaded once network connectivity is regained
     * @param pair a [Pair] containing the old and modified [Task] objects.
     */
    private fun addTaskToUpload(pair: Pair<Task?, Task>) {
        val file = File(context.filesDir, "tasksToUpload.json")
        if (!file.exists()) {
            file.createNewFile()
            Files.asCharSink(file, Charsets.UTF_8).write("[]")
        }

        val currentTasksToUpload = getTasksToUpload()
        currentTasksToUpload.add(pair)
        Files.asCharSink(file, Charsets.UTF_8).write(gson.toJson(currentTasksToUpload))
    }

    /**
     * Returns a [List] of all differences between [Task] objects that are to be passed to
     * the server upon regaining network connectivity
     *
     * @return a [List] of [Pair] objects containing [Task] object
     */
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

    /**
     * Create a list of users that will be uploaded once network connectivity is regained
     * @param pair a [Pair] containing the old and modified [User] objects.
     */
    private fun addUserToUpload(pair: User) {
        val currentUsersToUpload = getUsersToUpload()
        currentUsersToUpload.add(pair)
        val file = File(context.filesDir, "usersToUpload.json")
        if (!file.exists()) {
            file.createNewFile()
            Files.asCharSink(file, Charsets.UTF_8).write("[]")
        }

        Files.asCharSink(file, Charsets.UTF_8).write(gson.toJson(currentUsersToUpload))
    }

    /**
     * Returns a [List] of all differences between [User] objects that are to be passed to
     * the server upon regaining network connectivity
     *
     * @return a [List] of [Pair] objects containing [User] object
     */
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

    /**
     * produces notifications whenever a [Task] in the [List] of tasks gets updated
     * @param tasks the [List] of [Task] objects to check for updates
     */
    private fun notifyUserOfBids(tasks: List<Task>) {
        val owner = getLocalUserName()
        val oldTasks = getTasksFromDisk().filter({ it -> it.owner == owner })
        var i = 1
        for (task in tasks.filter({ it -> it.owner == owner })) {
            for (old_task in oldTasks.filter { it -> (it.title == task.title && it.description == task.description) }) {
                for (bid in task.bids) {
                    if (!old_task.bids.contains(bid)) {
//                    if (i < 5) {
                        Log.i("NOTIFY_NEW_BID", task.title + bid.toString())
                        val CHANNEL_ID = "my_id_01"// The internal id of the channel.
                        val CHANNEL_NAME = "Bids"// The public name of the channel.
                        val viewTaskIntent = Intent(context, ViewTaskActivity::class.java)
                        val bundle = Bundle()
                        val strTask = GenerateRetrofit.generateGson().toJson(task)
                        bundle.putString("TASK", strTask)
                        viewTaskIntent.putExtras(bundle)
                        val pendingIntent = PendingIntent.getActivity(context, 0, viewTaskIntent, 0)
                        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                                .setContentTitle("New bid on ${task.title}!")
                                .setContentText("${bid.owner} made a bid of $${bid.amount} on your task!")
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setColor(0x855bf7)
                                .setLights(0x855bf7, 1, 1)
                                .setPriority(2)
                                .setVibrate(longArrayOf(1000, 0, 1000, 0, 1000, 1000, 0, 1000, 0, 1000, 0, 1000, 1000, 0, 1000, 0, 1000, 0, 1000, 1000, 0, 1000))

                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


                        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
                            notificationManager.createNotificationChannel(NotificationChannel(CHANNEL_ID,
                                    CHANNEL_NAME,
                                    NotificationManager.IMPORTANCE_HIGH))
                        }
                        notificationManager.notify(i, mBuilder.build())
                        i += 1
                    }

                }
            }
        }
    }

    /**
     * Return the server status of the server. A local cached copy will be returned if the network
     * is unavailable and an updated copy will be returned if the network is available
     * @property callback the [ServerInfo] instance that gets returned
     * @see [AsyncTask]
     */
    inner class getServerInfo(val callback: Callback<ServerInfo?>) : AsyncTask<Void, Void, ServerInfo?>() {
        private var resultFromCache: Boolean = false

        /**
         * Attempt to talk to the elasticsearch server and update data if a connection was established
         * @param result a [ServerInfo] instance
         */
        override fun onPostExecute(result: ServerInfo?) {
            super.onPostExecute(result)
            if (result == null) {
                callback.onFailure()
            } else {
                tryUploads()
                callback.onResponse(result, resultFromCache)
            }

        }

        /**
         * Function callback that gets executed asyncronously in the background
         * @param params the passed in parameters
         * @return a [ServerInfo] instance
         */
        override fun doInBackground(vararg params: Void): ServerInfo? {
            return server.getServerInfo().execute().body()
        }
    }


    /**
     * Return a [List] of [User] objects from either a cached source or via elasticsearch,
     * depending on if the network is available. If there are is any cached information that is
     * not on the server this information will be attempted to be uploaded
     *
     * @property callback the [List] of [User] objects to reference
     * @see [AsyncTask]
     */
    inner class getUsers(val callback: Callback<List<User>>) : AsyncTask<Void, Void, List<User>>() {

        private var resultFromCache: Boolean = false

        /**
         * Attempt to talk to the elasticsearch server
         * @param result a [List] of [User] object instance
         */
        override fun onPostExecute(result: List<User>) {
            super.onPostExecute(result)
            callback.onResponse(result, resultFromCache)
            if (!resultFromCache) {
                tryUploads()
            }
        }

        /**
         * Function callback that gets executed asyncronously in the background
         * @param params the passed in parameters
         * @return a [List] of [User] objects instance
         */
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

    /**
     * Return a [List] of [Task] objects from either a cached source or via elasticsearch,
     * depending on if the network is available. If there are is any cached information that is
     * not on the server this information will be attempted to be uploaded
     *
     * @property callback the [List] of [Task] objects to reference
     * @see [AsyncTask]
     */
    inner class getTasks(val callback: Callback<List<Task>>) : AsyncTask<Void, Void, List<Task>>() {

        private var resultFromCache: Boolean = false

        /**
         * Attempt to talk to the elasticsearch server
         * @param result a [List] of [Task] object instances
         */
        override fun onPostExecute(result: List<Task>) {
            super.onPostExecute(result)
            callback.onResponse(result, resultFromCache)
            if (!resultFromCache) {
                tryUploads()
            }

        }

        /**
         * Function callback that gets executed asyncronously in the background
         * @param params the passed in parameters
         * @return a [List] of [User] objects instance
         */
        override fun doInBackground(vararg params: Void): List<Task> {
            var tasks: List<Task>
            try {
                val t = server.getTasks().execute().body()
                if (t == null) {
                    resultFromCache = true
                    tasks = getTasksFromDisk()
                } else {
                    tasks = t
                    notifyUserOfBids(tasks)
                    setTasks(tasks)
                }
            } catch (e: Throwable) {
                resultFromCache = true
                tasks = getTasksFromDisk()

            }
            println(tasks)
            return tasks
        }
    }

    /**
     * Attempt to update a task. If the task was succesfully updated, attempt to upload any other
     * cached data to the elasticsearch server
     * @property callback a [Boolean] that gets updated indicating whether the option was a success
     * or not
     */
    inner class updateTask(val callback: Callback<Boolean>) : AsyncTask<Pair<Task?, Task>, Void, Boolean>() {

        /**
         * Attempt to talk to the elasticsearch server
         * @param uploadSucceeded a [Boolean] indicating if the upload was a success
         */
        override fun onPostExecute(uploadSucceeded: Boolean) {
            super.onPostExecute(uploadSucceeded)
            if (uploadSucceeded) {
                tryUploads()
                callback.onResponse(uploadSucceeded, uploadSucceeded)
            } else {
                callback.onFailure()
            }
        }

        /**
         * Function callback that gets executed asyncronously in the background
         * @param params the passed in parameters
         * @return a [Boolean] indicating results
         */
        override fun doInBackground(vararg params: Pair<Task?, Task>): Boolean {
            var uploadSuccessful = true
            for (pair in params) {
                Log.i("TASK", pair.toString())

                try {
                    val (old, new) = pair
                    if (old == null) {
                        server.createTask(new).execute()
                    } else {
                        val query = Query.taskQuery(old.owner, old.title, old.description)
                        val id = server.getTaskID(query).execute().body()
                        if (id == null) {
                            addTaskToUpload(pair)
                            uploadSuccessful = false
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

    /**
     * Attempt to update a user. If the user was succesfully updated, attempt to upload any other
     * cached data to the elasticsearch server
     * @property callback a [Boolean] that gets updated indicating whether the option was a success
     * or not
     */
    inner class updateUser(val callback: Callback<Boolean>) : AsyncTask<User, Void, Boolean>() {

        /**
         * Attempt to talk to the elasticsearch server
         * @param uploadSucceeded a [Boolean] indicating if the upload was a success
         */
        override fun onPostExecute(uploadSucceeded: Boolean) {
            super.onPostExecute(uploadSucceeded)
            if (uploadSucceeded) {
                tryUploads()
                callback.onResponse(uploadSucceeded, uploadSucceeded)
            } else {
                callback.onFailure()
            }
        }

        /**
         * Function callback that gets executed asyncronously in the background
         * @param params the passed in parameters
         * @return a [Boolean] indicating results
         */
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

    /**
     * Attempt to upload any new data that needs to be pushed to the server still
     */
    private fun tryUploads() {
        val callback = object : Callback<Boolean> {
            override fun onResponse(response: Boolean, responseFromCache: Boolean) {}
        }

        for (user in getUsersToUpload()) {
            updateUser(callback).execute(user)
        }
        for (task in getTasksToUpload()) {
            updateTask(callback).execute(task)
        }
    }

    /**
     * Return whether the server is online or not
     * @return the status of the server
     */
    fun isOnline(): Boolean {
        TODO("not implemented")
    }


}
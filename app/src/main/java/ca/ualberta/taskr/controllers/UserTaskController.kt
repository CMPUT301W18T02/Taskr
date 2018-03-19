// TODO: For some reason this file isnt recognized as being part of the project. WTF James
// TODO: Deletion from server
// TODO: Upload changes to server

package ca.ualberta.taskr.controllers

import android.accounts.NetworkErrorException
import ca.ualberta.taskr.exceptions.InvalidTypeException
import ca.ualberta.taskr.exceptions.ResourceDoesNotExistException
import ca.ualberta.taskr.exceptions.UserAlreadyExistsException
import ca.ualberta.taskr.exceptions.UserDoesNotExistException
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.*
import com.google.gson.reflect.TypeToken
import com.mapbox.mapboxsdk.geometry.LatLng
import okhttp3.RequestBody

/**
 * Created by james on 25/02/18.
 *
 * UserTaskController Class. This class acts to provide a way to map from users to tasks in
 * a bidirectional manner. Both directions are searchable.
 */
data class UserTaskController(var userMap: HashMap<User, ArrayList<Task>>) {

    private val query: ElasticSearch = GenerateRetrofit.generateRetrofit()
    private val tasksToUpload: HashMap<RequestBody, Task> = HashMap()
    private val usersToUpload: HashMap<RequestBody, User> = HashMap()
    private val tasksToDelete: ArrayList<RequestBody> = ArrayList()

    /**
     * returns true if user exists
     */
    fun doesUserExist(username: String): Boolean {
        return userMap.keys.any { it.username == username }
    }

    /**
     * Add a user with a supplied user object
     */
    fun addUser(user: User) {
        if (doesUserExist(user.username)) {
            throw UserAlreadyExistsException(user.username)
        } else {
            userMap[user] = ArrayList()
        }
    }

    /**
     * Add a task with a supplied task object
     *
     * @throws UserDoesNotExistException when the owner of the task is not a user
     */
    fun addTask(task: Task) {
        val user = getUserFromUsername(task.owner)
        val tasks = userMap[user] ?: throw UserDoesNotExistException(user.username)
        tasks.add(task)
    }

    /**
     * Get all of the tasks owned by a user
     */
    fun getUserTasks(user: User): List<Task> {
        return userMap[user] ?: throw UserDoesNotExistException(user.username)
    }

    /**
     * Get all tasks
     */
    fun getAllTasks(): ArrayList<Task> {
        val taskCollection = userMap.values
        val tasks = ArrayList<Task>()
        for (list in taskCollection) {
            tasks.addAll(list)
        }

        return tasks
    }

    /**
     * Get tasks within 5 km of the location specified
     */
    fun getNearbyTasks(location: LatLng): List<Task> {
        return getAllTasks().filter { location.distanceTo(it.location) <= 5000 }
    }

    /**
     * Delete task
     */
    fun removeTask(task: Task) {
        userMap[getUserFromUsername(task.owner)]?.remove(task)
    }

    /**
     * Upload all changes to the server
     */
    fun uploadChanges() {
        deleteTasksFromServer()
        updateTasksOnServer()
        updateUsersOnServer()
    }

    private fun updateUsersOnServer() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updateTasksOnServer() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun deleteTasksFromServer() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getID(item: MutableMap.MutableEntry<RequestBody, Any>, type: Class<out Any>): ElasticsearchID {
        return when (type) {
            object : TypeToken<User>() {}.javaClass -> query.getUserID(item.key).execute().body()
                    ?: throw ResourceDoesNotExistException()
            object : TypeToken<Task>() {}.javaClass -> query.getTaskID(item.key).execute().body()
                    ?: throw ResourceDoesNotExistException()
            else -> throw InvalidTypeException()
        }
    }

    /**
     * Download all users and tasks from the server
     */
    fun downloadChanges() {
        val newUsers = query.getUsers().execute().body()
        val newMap = HashMap<User, ArrayList<Task>>()

        val newTasks = query.getTasks().execute().body()
        if (newUsers == null || newTasks == null) {
            throw NetworkErrorException("Users or Tasks failed to download")
        }
        for (user in newUsers) {
            val userTasks = ArrayList(newTasks.filter { it.owner == user.username })
            newMap[user] = userTasks
        }
        userMap = newMap
    }

    /**
     * Return true if the database is available
     */
    fun checkDataBaseConnectivity(): Boolean {
        val queryCall = query.getServerInfo()
        val info = queryCall.execute().body()
        return if (info?.status == null) false else !info.timed_out
    }

    /**
     * Get user object from username
     */
    fun getUserFromUsername(username: String): User {
        val users = userMap.keys.filter { it.username == (username) }

        if (users.isNotEmpty()) {
            return users[0]
        } else {
            throw UserDoesNotExistException(username)
        }
    }

}

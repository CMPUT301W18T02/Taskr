// TODO: For some reason this file isnt recognized as being part of the project. WTF James

package ca.ualberta.taskr.models

import android.accounts.NetworkErrorException
import ca.ualberta.taskr.models.elasticsearch.ElasticSearch
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * Created by james on 25/02/18.
 *
 * UserTaskController Class. This class acts to provide a way to map from users to tasks in
 * a bidirectional manner. Both directions are searchable.
 */
data class UserTaskController(var userMap: HashMap<User, ArrayList<Task>>) {

    private val query: ElasticSearch = GenerateRetrofit.generateRetrofit()


    fun doesUserExist(username: String): Boolean {
        return userMap.keys.any { it.username == username }
    }
    fun addUser(user: User) {
        if (doesUserExist(user.username)) {
            throw UserAlreadyExistsException(user.username)
        }
        else {
            userMap[user] = ArrayList()
        }
    }

    fun addTask(user: User, task: Task){
        val tasks = userMap[user] ?: throw UserDoesNotExistException(user.username)
        tasks.add(task)
    }

    fun getUserTasks(user: User): List<Task> {
        return userMap[user] ?: throw UserDoesNotExistException(user.username)

    }

    fun getAllTasks(): ArrayList<Task> {
        val taskCollection = userMap.values
        val tasks = ArrayList<Task>()
        for (list in taskCollection) {
            tasks.addAll(list)
        }

        return tasks// temporary for compilation
    }

    fun getNearbyTasks(location: LatLng): List<Task> {
        return getAllTasks().filter { location.distanceTo(it.location) <= 5000 }
    }

    fun removeTask(user: User, task: Task) {

    }

    fun uploadChanges() {}

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

    fun checkDataBaseConnectivity(): Boolean {
        val queryCall = query.getServerInfo()
        val info = queryCall.execute().body()
        return if (info?.status == null) false else info.status == "green"
    }

    fun getUserFromUsername(username: String): User {
        val users = userMap.keys.filter { it.username == (username) }

        if (users.isNotEmpty()) {
            return users[0]
        } else {
            throw UserDoesNotExistException(username)
        }
    }

}
class UserDoesNotExistException(message: String) : Exception("User: $message does not exist")

class UserAlreadyExistsException(message: String) : Exception("User: $message already exists")


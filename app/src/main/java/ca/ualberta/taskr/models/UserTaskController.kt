package ca.ualberta.taskr.models

import android.drm.DrmStore
import android.media.Image
import com.mapbox.mapboxsdk.geometry.LatLng
import com.searchly.jestdroid.DroidClientConfig
import com.searchly.jestdroid.JestClientFactory
import io.searchbox.action.Action
import io.searchbox.client.JestClient
import io.searchbox.client.JestResult
import io.searchbox.core.Get

/**
 * Created by james on 25/02/18.
 */
data class UserTaskController(val userMap: HashMap<User, ArrayList<Task>>) {
    private val factory = JestClientFactory()
    private var serverUri = "http://cmput301.softwareprocess.es:8080/cmput301w18t02"

    constructor(userMap: HashMap<User, ArrayList<Task>>, serverUri: String) : this(userMap) {
        this.serverUri = serverUri
    }

    init {
        factory.setDroidClientConfig(DroidClientConfig
                .Builder(serverUri)
                .multiThreaded(true)
                .defaultMaxTotalConnectionPerRoute(3)
                .maxTotalConnection(20)
                .build())
    }

    val client = factory.`object`

    fun addUser(usr: User) {}

    fun addTask() {}

    fun getTaskList(): ArrayList<Task> {
        return ArrayList<Task>() // temporary for compilation
    }

    fun getAllTasks(): ArrayList<Task> {
        downloadChanges()

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

    fun removeTask() {}

    fun uploadChanges() {}

    fun downloadChanges() {}

    fun checkDataBaseConnectivity(): Boolean {
        val result = client.execute(Get.Builder("", "").build())
        return true
    }

    fun getUserFromUsername(username: String): User {
        val users = userMap.keys.filter { it.name == (username) }

        if (users.isNotEmpty()) {
            return users[0]
        } else {
            throw UserDoesNotExistException(username)
        }
        // end of temporary data
    }

    class UserDoesNotExistException(message: String) : Exception("User: $message does not exist")
}
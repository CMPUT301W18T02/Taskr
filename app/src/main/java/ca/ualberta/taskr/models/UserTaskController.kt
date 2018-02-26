package ca.ualberta.taskr.models

/**
 * Created by james on 25/02/18.
 */
data class UserTaskController(val UserMap: HashMap<User, ArrayList<Task>>) {

    fun addUser(){}

    fun addTask(){}

    fun getTaskList(){}

    fun getAllTasks(){}

    fun getNearbyTasks(){}

    fun removeTask(){}

    fun uploadChanges(){}

    fun downloadChanges(){}

    fun checkDataBaseConnectivity(){}

    fun getUserFromUsername(){}
}
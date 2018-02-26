package ca.ualberta.taskr.models

/**
 * Created by james on 25/02/18.
 */
data class UserTaskController(val UserMap: HashMap<User, ArrayList<Task>>) {

    fun addUser(usr:User){}

    fun addTask(){}

    fun getTaskList(): ArrayList<Task>{}

    fun getAllTasks(): ArrayList<Task>{}

    fun getNearbyTasks(): ArrayList<Task>{}

    fun removeTask(){}

    fun uploadChanges(){}

    fun downloadChanges(){}

    fun checkDataBaseConnectivity(): Boolean{}

    fun getUserFromUsername(username:String): User{}
}
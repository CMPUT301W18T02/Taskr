package ca.ualberta.taskr.models

import android.media.Image

/**
 * Created by james on 25/02/18.
 */
data class UserTaskController(val UserMap: HashMap<User, ArrayList<Task>>) {

    fun addUser(usr:User){}

    fun addTask(){}

    fun getTaskList(): ArrayList<Task>{
        return ArrayList<Task>() // temporary for compilation
    }

    fun getAllTasks(): ArrayList<Task>{
        return ArrayList<Task>() // temporary for compilation
    }

    fun getNearbyTasks(): ArrayList<Task>{
        return ArrayList<Task>() // temporary for compilation
    }

    fun removeTask(){}

    fun uploadChanges(){}

    fun downloadChanges(){}

    fun checkDataBaseConnectivity(): Boolean{
        return true // temporary for compilation
    }

    fun getUserFromUsername(username:String): User{
        // temporary data for compilation
        var name = "John"
        var email = "jsmith@ualberta.ca"
        var phoneNumber = "1234567890"
        var username = "jsmith"
        var image: Image? = null

        val usr = User(name, phoneNumber, image, email, username)

        return usr
        // end of temporary data
    }
}
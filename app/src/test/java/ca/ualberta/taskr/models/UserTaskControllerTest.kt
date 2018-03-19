package ca.ualberta.taskr.models

import ca.ualberta.taskr.controllers.UserTaskController
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.assertEquals

/**
 * Created by james on 25/02/18.
 */
@RunWith(RobolectricTestRunner::class)
class UserTaskControllerTest {

    private val map = HashMap<User, ArrayList<Task>>()

    var name = "John"
    var email = "jsmith@ualberta.ca"
    var phoneNumber = "1234567890"
    var username = "jsmith"
    var image: String? = null

    private val usr = User(name, phoneNumber, image, email, username)


    @Test
    fun testGetHashMap(){
        val usrTaskCtrl = UserTaskController(map)
        val hashMap = usrTaskCtrl.userMap
        assertEquals(map, hashMap)
    }

    @Test
    fun testAddUser(){
        val usrTaskCtrl = UserTaskController(map)
        usrTaskCtrl.addUser(usr)
        val otherUsr = usrTaskCtrl.getUserFromUsername(username)
        assertEquals(usr, otherUsr)
    }

    @Test
    fun testAddTask(){}

    @Test
    fun testGetTaskList(){}

    @Test
    fun testGetAllTasks(){}

    @Test
    fun testGetNearbyTasks(){}

    @Test
    fun testRemoveTask(){}

    @Test
    fun testUploadChanges(){}

    @Test
    fun testDownloadChanges(){}

    @Test
    fun testCheckDataBaseConnectivity(){}

    @Test
    fun testGetUserFromUsername(){
        val usrTaskCtrl = UserTaskController(map)
        usrTaskCtrl.addUser(usr)
        val otherUsr = usrTaskCtrl.getUserFromUsername(username)
        assertEquals(usr, otherUsr)
    }
}
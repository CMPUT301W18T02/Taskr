package ca.ualberta.taskr

import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.UserTaskController
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import ca.ualberta.taskr.models.Task
import org.junit.Assert.assertEquals

/**
 * Created by james on 25/02/18.
 */
@RunWith(RobolectricTestRunner::class)
class UserTaskControllerTest {

    private val map = HashMap<User, ArrayList<Task>>()

    @Test
    fun testGetHashMap(){
        val usrTaskCtrl = UserTaskController(map)
        val hashMap = usrTaskCtrl.UserMap
        assertEquals(map, hashMap)
    }

    @Test
    fun testAddUser(){}

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
    fun testGetUserFromUsername(){}
}
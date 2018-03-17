package ca.ualberta.taskr.models

import android.media.Image
import ca.ualberta.taskr.models.*
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before

/**
 * Created by james on 25/02/18.
 */
@RunWith(RobolectricTestRunner::class)
class UserTaskControllerTest {

    private val map = HashMap<User, ArrayList<Task>>()

    private var name = "John"
    private var email = "jsmith@ualberta.ca"
    private var phoneNumber = "1234567890"
    private var username = "jsmith"
    private var image: Image? = null


    private var owner = "Corkus"
    private var title = "I'm a wot"
    private var status: TaskStatus? = null
    private var bids = ArrayList<Bid>()
    private var description = "4 mana 7/7"
    private var photos = ArrayList<Image>()
    private var location0: LatLng = LatLng(70.3992,12.1234)
    private var location1: LatLng = LatLng(70.3413,12.1234)
    private var location2: LatLng = LatLng(71.3413,12.1894)


    private var chosenBidder = "The Mask"


    private val usr0 = User(name, phoneNumber, image, email, username)
    private val usr1 = User("corkus", phoneNumber, image, email, "Corkus")
    private val usr2 = User("Jim", "234728942", null, "jim@pretty.com", "jim")

    private val task0 = Task(owner, title, status, bids, description, photos, location0, chosenBidder)
    private val task1 = Task(usr2.username, "Buy my stuff", status, bids, description, photos, location1, chosenBidder)
    private val task2 = Task(usr2.username, "Mow my lawn", status, bids, description, photos, location2, chosenBidder)


    @Before
    fun before() {
        map.clear()
    }

    @Test
    fun testGetHashMap() {
        val usrTaskCtrl = UserTaskController(map)
        val hashMap = usrTaskCtrl.userMap
        assertEquals(map, hashMap)
    }

    @Test
    fun testAddUser() {
        val ctl = UserTaskController(map)
        ctl.addUser(usr0)
        assertEquals(usr0, ctl.getUserFromUsername(username))
    }

    @Test
    fun testAddTask() {
        val ctl = UserTaskController(map)
        ctl.addUser(usr1)
        ctl.addTask(task0)
        assertTrue(ctl.getAllTasks().contains(task0))
    }

    @Test
    fun testGetUserTasks() {
        map[usr1] = arrayListOf(task0)
        val ctl = UserTaskController(map)
        val tasks = ctl.getUserTasks(usr1)
        assertEquals(arrayListOf(task0), tasks)
    }

    @Test
    fun testGetAllTasks() {

        map[usr1] = arrayListOf(task0)
        map[usr2] = arrayListOf(task1, task2)

        val ctl = UserTaskController(map)
        val tasks = ctl.getAllTasks()
        assertEquals(arrayListOf(task0, task1, task2), tasks)

    }


    @Test
    fun testGetNearbyTasks() {
        map[usr1] = arrayListOf(task0)
        map[usr2] = arrayListOf(task1, task2)

        val ctl = UserTaskController(map)
        val here = LatLng(70.3416,12.1223)
        val tasks = ctl.getNearbyTasks(here)
        assertEquals(arrayListOf(task1), tasks)

    }

    @Test
    fun testRemoveTask() {
        map[usr2] = arrayListOf(task1, task2)

        val ctl = UserTaskController(map)
        ctl.removeTask(task1)
        val tasks = ctl.getAllTasks()
        assertEquals(arrayListOf(task2), tasks)

    }

    @Test
    fun testGetUserFromUsername() {
        map[usr0] = ArrayList()
        val usrTaskCtrl = UserTaskController(map)
        val otherUsr = usrTaskCtrl.getUserFromUsername(username)
        assertEquals(usr0, otherUsr)
    }
}
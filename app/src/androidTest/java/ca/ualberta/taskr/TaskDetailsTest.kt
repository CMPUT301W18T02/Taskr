package ca.ualberta.taskr

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

/**
 * Created by marissasnihur on 2018-04-06.
 *
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(26))
class TaskDetailsTest {

    private val taskTitle = "Test Title for a Task"
    private val taskDescr = "This is a description for a task. I am describing a task."
    private val taskLatLng = LatLng(80.0, 80.0)
    private val taskLocStr: String = taskLatLng.toString()

    private var taskList: ArrayList<Task> = ArrayList()
    private var shownTaskList: ArrayList<Task> = ArrayList()

    private lateinit var activity: ViewTaskActivity
    private lateinit var intent: Intent

    private var owner = "Corkus"
    private var bidder = "BLAH"
    private var status: TaskStatus? = null
    private var bids = java.util.ArrayList<Bid>()
    private var photos = java.util.ArrayList<String>()
    private var chosenBidder = "The Mask"

    private lateinit var taskAuthor: TextView
    lateinit var taskTitleView: TextView
    lateinit var taskDetails: TextView
    lateinit var taskStatus: TextView
    lateinit var lowestBidView: TextView
    lateinit var bid: Bid

    private lateinit var task: Task

    private var lowestBid : Double = Double.POSITIVE_INFINITY
    private val username = "TestUsername"


    @Before
    fun setUp(){


        bid = Bid(bidder, 100.00, false)

        bids.add(bid)

        task = Task(owner, taskTitle, status, bids, taskDescr, photos, taskLatLng, chosenBidder)

        taskList.add(task)
        shownTaskList.add(task)

        val position = 0
        val intent = Intent(ShadowApplication.getInstance().applicationContext, ViewTaskActivity::class.java)
        val bundle = Bundle()
        val strTask = GenerateRetrofit.generateGson().toJson(shownTaskList[position])
        bundle.putString("TASK", strTask)




        //TODO: Stop Mapbox from breAKING THINGSSS
        val editTaskActivity = Robolectric.setupActivity(EditTaskActivity::class.java)

        UserController(editTaskActivity).setLocalUsername(username)

        activity = Robolectric.buildActivity(ViewTaskActivity::class.java, intent).create().visible().get()

        taskAuthor = activity.findViewById<TextView>(R.id.taskAuthorText)
        taskTitleView = activity.findViewById(R.id.taskTitle)
        taskDetails = activity.findViewById(R.id.taskDetails)
        taskStatus = activity.findViewById(R.id.taskStatus)
        lowestBidView = activity.findViewById(R.id.taskLowestBid)


    }

    @Test
    fun viewDetailsTest(){

        for(tempBid : Bid in bids){
            tempBid.amount < lowestBid
            lowestBid = tempBid.amount
        }

        Assert.assertTrue(task.owner == taskAuthor.toString())
        Assert.assertTrue(task.title == taskTitleView.toString())
        Assert.assertTrue(lowestBid.toString() == lowestBidView.toString())


    }

    @Test
    fun taskStatusTest(){
        Assert.assertTrue(true)
    }

}
package ca.ualberta.taskr

import android.support.v7.widget.RecyclerView
import android.util.Log
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.*
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import retrofit2.Call
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import retrofit2.Callback
import android.content.Intent
import ca.ualberta.taskr.controllers.UserController
import org.junit.Before
import android.app.Activity
import android.support.test.InstrumentationRegistry






/**
 * Created by James Cook on 2018-04-07.
 *
 * This test class deals with all of the tests that involve bidding.
 */
@RunWith(AndroidJUnit4::class)
class TaskBiddingTests {

    private val taskTitle = "Test Title for a Task"
    private val taskDescr = "This is a description for a task. I am describing a task."
    private val taskLatLng = LatLng(80.0, 80.0)
    private val taskLocStr: String = taskLatLng.toString()

    private lateinit var testTask: Task

    private val username = "TestUsername"
    private val taskPosterUsername = "TestTaskUsername"
    private lateinit var myBidsActivity: MyBidsActivity
    private lateinit var viewTaskActivity: ViewTaskActivity

    private lateinit var bidListView: RecyclerView


    @Rule
    @JvmField
    val rule = ActivityTestRule<ViewTaskActivity>(ViewTaskActivity::class.java, false, true)
    private lateinit var launchedActivity: Activity

    @Before
    fun setup() {
        createTestTask()
    }



    /**
     * Posts a dummy task to be bid on.
     */
   private fun createTestTask(){
        testTask = Task(owner=taskPosterUsername,
                title=taskTitle,
                status= TaskStatus.REQUESTED,
                bids=ArrayList(),
                description=taskDescr,
                photos=ArrayList(),
                location=taskLatLng,
                chosenBidder="")

        GenerateRetrofit.generateRetrofit().createTask(testTask)

        Thread.sleep(1000)
    }

    /**
     * Delete the test Task.
     */
    private fun deleteTestTask(){
        //delete test task in elastic search
        GenerateRetrofit.generateRetrofit().getTaskID(Query.taskQuery(taskPosterUsername, taskTitle, taskDescr))
                .enqueue(object : Callback<ElasticsearchID> {
                    override fun onResponse(call: Call<ElasticsearchID>, response: Response<ElasticsearchID>) {
                        Log.i("network", response.body().toString())
                        val id = response.body() as ElasticsearchID
                        GenerateRetrofit.generateRetrofit().deleteTask(id.toString())
                    }

                    override fun onFailure(call: Call<ElasticsearchID>, t: Throwable) {
                        Log.e("network", "Network Failed!")
                        t.printStackTrace()
                        return
                    }
                })
    }

    /**
     * US 05.01.01
     *
     * As a task provider, I want to make a bid on a given task with status: requested or bidded,
     * using a monetary amount.
     *
     * Post a task under one name, bid on said task through new name.
     */
    @Test
    fun makeBid(){

        val i = Intent()
        val taskStr = GenerateRetrofit.generateGson().toJson(testTask)
        i.putExtra("TASK", taskStr)
        launchedActivity = rule.launchActivity(i)
        rule.activity.supportFragmentManager.beginTransaction()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        UserController(context).setLocalUsername(username)

        onView(withId(R.id.addBidOrMarkDone)).perform(click())

        deleteTestTask()
    }

    /**
     * US 05.02.01 (revised 2018-02-14)
     * As a task provider, I want to view a list of tasks that I have bidded on, each with its task
     * requester username, title, status, lowest bid so far, and my bid.
     *
     * Check if bids on server associated with username is the same as those in list.
     */
    @Test
    fun viewListOfBids(){

    }

    /**
     * US 05.03.01
     * As a task requester, I want to be notified of a bid on my tasks.
     *
     * Check if user is notified on bid.
     */
    @Test
    fun notificationOnBid(){

    }

     /**
     * US 05.04.01
     * As a task requester, I want to view a list of my tasks with status bidded, each having one
     * or more bids.
     */
    @Test
    fun viewListOfMyTasksWithBids(){

     }

    /**
     * US 05.05.01
     * As a task requester, I want to view the bids on one of my tasks.
     */
    @Test
    fun viewBidsOnMyTask(){

    }

    /**
     * US 05.06.01
     * As a task requester, I want to accept a bid on one of my tasks, setting its status to
     * assigned, and clearing any other bids on that task.
     */
    @Test
    fun acceptBid(){

    }

    /**
     * US 05.07.01
     * As a task requester, I want to decline a bid on one of my tasks.
     */
    @Test
    fun declineBid(){

    }
}
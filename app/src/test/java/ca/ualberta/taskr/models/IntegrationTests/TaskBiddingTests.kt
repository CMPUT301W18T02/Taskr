package ca.ualberta.taskr.models.IntegrationTests

import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.EditText
import android.widget.RelativeLayout
import butterknife.BindView
import ca.ualberta.taskr.*
import ca.ualberta.taskr.adapters.TaskListAdapter
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowActivity
import org.robolectric.shadows.ShadowLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by James Cook on 2018-04-07.
 *
 * This test class deals with all of the tests that involve bidding.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(26))
class TaskBiddingTests {

    private val taskTitle = "Test Title for a Task"
    private val taskDescr = "This is a description for a task. I am describing a task."
    private val taskLatLng = LatLng(80.0, 80.0)
    private val taskLocStr: String = taskLatLng.toString()

    private val username = "TestUsername"
    private val taskPosterUsername = "TestTaskUsername"
    private lateinit var myBidsActivity: MyBidsActivity
    private lateinit var viewTaskActivity: ViewTaskActivity

    private lateinit var bidListView:RecyclerView

    /**
     * Sets up all of the information needed by the test class.
     */

    @Before
    fun setUp(){
        viewTaskActivity = Robolectric.setupActivity(ViewTaskActivity::class.java)

        UserController(viewTaskActivity).setLocalUsername(username)

        ShadowLog.stream = System.out

        //Setup views
        bidListView = viewTaskActivity.findViewById(R.id.bidListView)
    }

    /**
     * Posts a dummy task to be bid on.
     */
   private fun createTestTask(){
        val testTask = Task(owner=taskPosterUsername,
                title=taskTitle,
                status=TaskStatus.REQUESTED,
                bids=ArrayList(),
                description=taskDescr,
                photos=ArrayList(),
                location=taskLatLng,
                chosenBidder="")
        // post testTask to servers
        // if a task has been passed in, edit its properties, otherwise post a new task
        // Taken from EditTaskActivity posting a task
//        CachingRetrofit(this).updateTask(object : ca.ualberta.taskr.models.elasticsearch.Callback<Boolean> {
//            override fun onResponse(response: Boolean, responseFromCache: Boolean) {
//                //TODO Deal with offline
//                Log.i("UPLOADED?", response.toString())
//            }
//        }).execute(Pair(testTask, testTask))

        Thread.sleep(1000)
    }

    /**
     * Delete the test Task.
     */
    private fun deleteTestTask(){
        //delete test task in elastic search
        GenerateRetrofit.generateRetrofit().getTaskID(Query.taskQuery(username, taskTitle, taskDescr))
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
     * Delete the test user.
     */
    private fun deleteTestUser(){
        //delete test user in elastic search, @JamesCook
        GenerateRetrofit.generateRetrofit().getUserID(Query.userQuery(username))
                .enqueue(object : Callback<ElasticsearchID> {
                    override fun onResponse(call: Call<ElasticsearchID>, response: Response<ElasticsearchID>) {
                        Log.i("network", response.body().toString())
                        val id = response.body() as ElasticsearchID
                        GenerateRetrofit.generateRetrofit().deleteUser(id.toString())
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
        createTestTask()

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
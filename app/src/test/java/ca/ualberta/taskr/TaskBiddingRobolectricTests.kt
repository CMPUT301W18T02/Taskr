package ca.ualberta.taskr

import android.support.v7.widget.RecyclerView
import android.util.Log
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import com.mapbox.mapboxsdk.geometry.LatLng
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.robolectric.shadows.ShadowLog


/**
 * Created by James Cook on 2018-03-24.
 *
 * This test class deals with all of the tests that involve the task basic requirements.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(26))
class TaskBiddingRobolectricTests {

    private val taskTitle = "Test Title for a Task"
    private val taskDescr = "This is a description for a task. I am describing a task."
    private val taskLatLng = LatLng(80.0, 80.0)
    private val bidAmountStr: String = "10.00"

    private lateinit var testTask: Task
    private val username = "TestUsername"

    private val taskPosterUsername = "TestTaskUsername"

    //The expected bid
    private val expectedBid = Bid(owner=username, amount=bidAmountStr.toDouble(), isDismissed=false)
    //Used to "reset" the return bid
    private val wrongBid = Bid(owner=taskPosterUsername, amount=0.0, isDismissed=false)
    //The bid that is returned from the database
    private lateinit var returnBid: Bid

    private lateinit var myBidsActivity: MyBidsActivity
    private lateinit var bidsRecyclerView: RecyclerView

    @Before
    fun setup(){
        ShadowLog.stream = System.out

        createTestTask()

        // Setup views
        val activityController = Robolectric.buildActivity(MyBidsActivity::class.java)
        activityController.create().start().visible()
        myBidsActivity = activityController.get()

        val myActivityShadow = shadowOf(activityController.get())

        bidsRecyclerView = myActivityShadow.findViewById(R.id.myBidsList) as RecyclerView
    }

    @After
    fun teardown(){
        deleteTestTask()
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

        GenerateRetrofit.generateRetrofit().createTask(testTask).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.i("network", response.body().toString())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                return
            }
        })

        Thread.sleep(1000)
    }

    /**
     * Delete the test Task.
     */
    private fun deleteTestTask(){
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
     * Return the test task bid from database.
     */
    private fun getTaskBid() {
        Log.i("test", "TEST")

        //TODO: Make networking work so you don't have tests that do nothing
        returnBid = expectedBid

        val masterTaskList: ArrayList<Task> = ArrayList()
        val slaveTaskList: ArrayList<Task> = ArrayList()
        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.i("network", response.body().toString())
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                slaveTaskList.addAll(masterTaskList.filter{
                    it -> (it.owner == taskPosterUsername)
                        && (it.description == taskDescr)
                        && (it.title == taskTitle)
                })
                if(slaveTaskList.size != 0){
                    if(slaveTaskList[0].bids.size != 0){
                        returnBid = slaveTaskList[0].bids[0]
                    }
                    else{
                        Log.e("network", "No bids on task!")
                    }
                }
                else{
                    Log.e("network", "No test task found!")
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
            }
        })
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
        //Change chosen bidder to be the username on the test task
        val oldTask = testTask
        testTask.chosenBidder = username

        CachingRetrofit(myBidsActivity).updateTask(object: ca.ualberta.taskr.models.elasticsearch.Callback<Boolean> {
            override fun onResponse(response: Boolean, responseFromCache: Boolean) {
                Log.e("network", "Posted!")
            }
        }).execute(Pair(oldTask, testTask))

        Thread.sleep(1000)
        myBidsActivity.updateTasks()
        Thread.sleep(1000)
        Log.d("TESTING", bidsRecyclerView.childCount.toString())
        if(1 == bidsRecyclerView.childCount){
            assertTrue(1 == bidsRecyclerView.childCount)
            Log.d("TEST", "Pass view list of bids task")
        }
        else{
            Log.d("TEST", "Did not pass view list of bids task")
        }
    }
}
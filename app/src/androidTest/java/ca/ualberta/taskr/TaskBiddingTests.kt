package ca.ualberta.taskr

import android.Manifest
import android.support.v7.widget.RecyclerView
import android.util.Log
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.*
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.runner.RunWith
import retrofit2.Response
import retrofit2.Call
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import retrofit2.Callback
import android.content.Intent
import ca.ualberta.taskr.controllers.UserController
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.rule.GrantPermissionRule
import ca.ualberta.taskr.models.Bid
import org.junit.*
import android.support.test.espresso.

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
    private lateinit var viewTaskActivity: ViewTaskActivity

    private lateinit var bidListView: RecyclerView


    @Rule
    @JvmField
    val viewTaskActivityRule = ActivityTestRule<ViewTaskActivity>(ViewTaskActivity::class.java, false, false)

    @Rule
    @JvmField
    val myBidsActivityRule = ActivityTestRule<MyBidsActivity>(MyBidsActivity::class.java, false, false)

    @get:Rule var permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION,
                                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                                            Manifest.permission.INTERNET)


    /**
     * Create a task, setup the activity and launch the activity before every test.
     */
    @Before
    fun setup() {
        createTestTask()
    }

    /**
     * Destroy the created task.
     */
    @After
    fun takeDown(){
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
     * US 05.01.01
     *
     * As a task provider, I want to make a bid on a given task with status: requested or bidded,
     * using a monetary amount.
     *
     * Post a task under one name, bid on said task through new name.
     */
    @Test
    fun makeBid(){
        val taskStr = GenerateRetrofit.generateGson().toJson(testTask)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val i = Intent(context, ViewTaskActivity::class.java)
        i.putExtra("TASK", taskStr)
        viewTaskActivity = viewTaskActivityRule.launchActivity(i)
        viewTaskActivityRule.activity.supportFragmentManager.beginTransaction()
        UserController(viewTaskActivity).setLocalUsername(username)


        onView(withId(R.id.addBidOrMarkDone)).perform(scrollTo(), click())
        onView(withId((R.id.enterAmountEdit))).perform(replaceText(bidAmountStr))
        onView(withId(R.id.confirm)).perform(click())
        Thread.sleep(1000)
        getTaskBid()
        Assert.assertEquals(expectedBid.toString(), returnBid.toString())
        Assert.assertTrue(expectedBid.amount == returnBid.amount)
        returnBid = wrongBid
        println(bidStr)
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
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val i = Intent(context, MyBidsActivity::class.java)
        myBidsActivity = myBidsActivityRule.launchActivity(i)

        //Change bidder to the username
        val oldTask = testTask
        testTask.chosenBidder = username
        CachingRetrofit(this).updateTask(object: ca.ualberta.taskr.models.elasticsearch.Callback<Boolean> {
            override fun onResponse(response: Boolean, responseFromCache: Boolean) {
                Log.e("network", "Posted!")
            }
        }).execute(Pair(oldTask, testTask))
        //R.id.myBidsList
        //onData(allOf(is(instanceOf(Map.class)), hasEntry(equalTo("STR"), is("item: 50"))).perform(click());
        onView(withId(R.id.myBidsList)).perform(actionOnItemAtPosition(0, isDisplayed()))
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
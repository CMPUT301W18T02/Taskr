package ca.ualberta.taskr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.TextView
import butterknife.BindView
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.*
import org.junit.runner.RunWith
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 *
 * Created by marissasnihur on 2018-04-06. / help from James
 *
 */

@RunWith(AndroidJUnit4::class)
class TaskDetailsTest {

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
    val rule = ActivityTestRule<ViewTaskActivity>(ViewTaskActivity::class.java, false, false)

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
     * Posts a dummy task to be bid on.
     */
    private fun createTestTask(status: TaskStatus){
        testTask = Task(owner=taskPosterUsername,
                title=taskTitle,
                status= status,
                bids=ArrayList(),
                description=taskDescr,
                photos=ArrayList(),
                location=taskLatLng,
                chosenBidder="")

        GenerateRetrofit.generateRetrofit().createTask(testTask)

        Thread.sleep(1000)
    }

    /**
     * US 02.01.01
     *
     * As a task requester or provider, I want to view all the details for a given task, including
     * its title, description, status, and lowest bid so far (if any).
     */

    @Test
    fun viewDetailsTest(){
        val statusReq: TaskStatus = TaskStatus.REQUESTED

        createTestTask(statusReq)

        val taskStr = GenerateRetrofit.generateGson().toJson(testTask)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val i = Intent(context, ViewTaskActivity::class.java)
        i.putExtra("TASK", taskStr)
        rule.launchActivity(i)
        rule.activity.supportFragmentManager.beginTransaction()

        UserController(context).setLocalUsername(username)

        onView(withId(R.id.taskAuthorText)).check(matches(withText(taskPosterUsername)))
        onView(withId(R.id.taskDetails)).check(matches(withText(taskDescr)))
        onView(withId(R.id.taskTitle)).check(matches(withText(taskTitle)))
        onView(withId(R.id.taskStatus)).check(matches(withText(TaskStatus.REQUESTED.toString())))

        if(testTask.bids.size == 0){
            onView(withId(R.id.taskPay)).check(matches(withText("")))
            return
        }
        else {
            var lowestBidAmount = Double.POSITIVE_INFINITY
            for (bid: Bid in testTask.bids) {
                if (lowestBidAmount > bid.amount) {
                    lowestBidAmount = bid.amount
                }
            }
            onView(withId(R.id.taskPay)).check(matches(withText(lowestBidAmount.toString())))
        }
        deleteTestTask()

    }


    /**
     * US 02.02.01
     *
     * As a task requester or provider, I want a task to have a status to be one of: requested,
     * bidded, assigned, or done.
     *
     * Bidded means at least one provider bidded on the task.
     */

    @Test
    fun taskStatusRequestedTest(){

        val statusRequested: TaskStatus = TaskStatus.REQUESTED

        testTask = Task(owner=taskPosterUsername,
                title=taskTitle,
                status= statusRequested,
                bids=ArrayList(),
                description=taskDescr,
                photos=ArrayList(),
                location=taskLatLng,
                chosenBidder="")

        GenerateRetrofit.generateRetrofit().createTask(testTask)

        Thread.sleep(1000)

        val taskStr = GenerateRetrofit.generateGson().toJson(testTask)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val i = Intent(context, ViewTaskActivity::class.java)
        i.putExtra("TASK", taskStr)
        rule.launchActivity(i)
        rule.activity.supportFragmentManager.beginTransaction()

        UserController(context).setLocalUsername(username)

        onView(withId(R.id.taskStatus)).check(matches(withText(statusRequested.toString())))


    }

    @Test
    fun taskStatusBidTest(){
        val statusBidded: TaskStatus = TaskStatus.BID

        testTask = Task(owner=taskPosterUsername,
                title=taskTitle,
                status= statusBidded,
                bids=ArrayList(),
                description=taskDescr,
                photos=ArrayList(),
                location=taskLatLng,
                chosenBidder="")

        GenerateRetrofit.generateRetrofit().createTask(testTask)

        Thread.sleep(1000)

        val taskStr = GenerateRetrofit.generateGson().toJson(testTask)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val i = Intent(context, ViewTaskActivity::class.java)
        i.putExtra("TASK", taskStr)
        rule.launchActivity(i)
        rule.activity.supportFragmentManager.beginTransaction()

        UserController(context).setLocalUsername(username)

        onView(withId(R.id.taskStatus)).check(matches(withText(statusBidded.toString())))

    }

    @Test
    fun taskStatusAssignedTest(){

        val statusAssigned: TaskStatus = TaskStatus.ASSIGNED

        testTask = Task(owner=taskPosterUsername,
                title=taskTitle,
                status= statusAssigned,
                bids=ArrayList(),
                description=taskDescr,
                photos=ArrayList(),
                location=taskLatLng,
                chosenBidder="")

        GenerateRetrofit.generateRetrofit().createTask(testTask)

        Thread.sleep(1000)

        val taskStr = GenerateRetrofit.generateGson().toJson(testTask)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val i = Intent(context, ViewTaskActivity::class.java)
        i.putExtra("TASK", taskStr)
        rule.launchActivity(i)
        rule.activity.supportFragmentManager.beginTransaction()

        UserController(context).setLocalUsername(username)

        onView(withId(R.id.taskStatus)).check(matches(withText(statusAssigned.toString())))

    }

    @Test
    fun taskStatusDoneTest(){

        val statusDone: TaskStatus = TaskStatus.DONE


        testTask = Task(owner=taskPosterUsername,
                title=taskTitle,
                status= statusDone,
                bids=ArrayList(),
                description=taskDescr,
                photos=ArrayList(),
                location=taskLatLng,
                chosenBidder="")

        GenerateRetrofit.generateRetrofit().createTask(testTask)

        Thread.sleep(1000)

        val taskStr = GenerateRetrofit.generateGson().toJson(testTask)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val i = Intent(context, ViewTaskActivity::class.java)
        i.putExtra("TASK", taskStr)
        rule.launchActivity(i)
        rule.activity.supportFragmentManager.beginTransaction()

        UserController(context).setLocalUsername(username)

        onView(withId(R.id.taskStatus)).check(matches(withText(statusDone.toString())))

    }

    /**
     * Delete's the test task after every test.
     */

    @After
    fun takeDown(){
        deleteTestTask()
    }

}
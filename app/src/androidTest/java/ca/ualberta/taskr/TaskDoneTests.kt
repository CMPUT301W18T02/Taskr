package ca.ualberta.taskr

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by marissasnihur on 2018-04-09.
 *
 */

@RunWith(AndroidJUnit4::class)
class TaskDoneTests {

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

    private fun createTestTask(status: TaskStatus){
        testTask = Task(owner=username,
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

    @Before
    fun setUp(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        UserController(context).setLocalUsername(username)
    }

    /**
     * US 07.01.01
     * As a task requester, I want to set a task with status:
     * assigned to have status: done, when it is completed.
     */

    @Test
    fun setAssignedToDone(){
        val statusReq: TaskStatus = TaskStatus.ASSIGNED



        createTestTask(statusReq)

        val taskStr = GenerateRetrofit.generateGson().toJson(testTask)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val i = Intent(context, ViewTaskActivity::class.java)
        i.putExtra("TASK", taskStr)
        rule.launchActivity(i)
        rule.activity.supportFragmentManager.beginTransaction()

        val userController = UserController(rule.activity)
        var username = userController.getLocalUserName()


        onView(withId(R.id.taskStatus)).check(matches(withText(TaskStatus.ASSIGNED.toString())))
        Assert.assertTrue(username == testTask.owner)



        onView(withId(R.id.addBidOrMarkDone)).perform(scrollTo(), click())

        Thread.sleep(2000)
        onView(withId(R.id.taskStatus)).check(matches(withText(TaskStatus.DONE.toString())))
    }

    /**
     * US 07.02.01
     *  As a task requester, I want to set a task with status: assigned to have status:
     *  requested, when it is not completed.
     */

    @Test
    fun setAssignedToRequested(){
        val statusReq: TaskStatus = TaskStatus.ASSIGNED

        createTestTask(statusReq)

        val taskStr = GenerateRetrofit.generateGson().toJson(testTask)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val i = Intent(context, ViewTaskActivity::class.java)
        i.putExtra("TASK", taskStr)
        rule.launchActivity(i)
        rule.activity.supportFragmentManager.beginTransaction()

        val userController = UserController(rule.activity)
        var username = userController.getLocalUserName()

        onView(withId(R.id.taskStatus)).check(matches(withText(TaskStatus.ASSIGNED.toString())))
        Assert.assertTrue(username == testTask.owner)

        onView(withId(R.id.reopenButton)).perform(scrollTo(), click())
        Thread.sleep(2000)
        onView(withId(R.id.taskStatus)).check(matches(withText(TaskStatus.REQUESTED.toString())))
    }


}
package ca.ualberta.taskr

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.EditText
import android.widget.RelativeLayout
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by marissasnihur on 2018-04-08.
 *
 */


@RunWith(RobolectricTestRunner::class)
class TaskRequesterAssignedTests {

    private var username = "TestUsername"
    private val taskTitle = "Test Title for a Task"
    private val taskDescr = "This is a description for a task. I am describing a task."

    private lateinit var activity: MyTasksActivity

    private lateinit var myTasksView: RecyclerView
    private lateinit var myTasksRefresh: SwipeRefreshLayout
    private lateinit var toolbar: Toolbar
    private lateinit var loadingPanel: RelativeLayout

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

    @Before
    fun setUp(){
        activity = Robolectric.setupActivity(MyTasksActivity::class.java)

        //set username
        UserController(activity).setLocalUsername(username)

        val userController = UserController(activity)
        username = userController.getLocalUserName()

        ShadowLog.stream = System.out

        //Setup views
        // addTaskButton = activity.findViewById<Button>(R.id.addTaskButton)
        myTasksView = activity.findViewById<RecyclerView>(R.id.myTasksView)
        toolbar = activity.findViewById<Toolbar>(R.id.myTasksToolbar)
        loadingPanel =  activity.findViewById<RelativeLayout>(R.id.loadingPanel)
        myTasksRefresh = activity.findViewById<SwipeRefreshLayout>(R.id.myTasksRefresh)
    }

    /**
     * US 06.02.01
     *
     * As a task requester, I want to view a list of my tasks with status: assigned,
     * each task with its task provider username, title, status, and accepted bid.
     */


    //TODO: Implement US 06.02.01
    @Test
    fun taskRequesterAssignedTest(){
        Assert.assertTrue(true)
    }


}
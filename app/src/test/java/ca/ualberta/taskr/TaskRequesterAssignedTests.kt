package ca.ualberta.taskr

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import ca.ualberta.taskr.adapters.TaskListAdapter
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import com.mapbox.mapboxsdk.geometry.LatLng
import junit.framework.Assert
import kotlinx.android.synthetic.main.row_task_item.view.*
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
    lateinit var taskSearchBar: EditText

    private var owner = "TaskOwner"
    private var title = "TaskTitle"
    private var status: TaskStatus = TaskStatus.REQUESTED
    private var bids = java.util.ArrayList<Bid>()
    private var description = "TaskDescription"
    private var photos = ArrayList<String>()
    private var location: LatLng = LatLng(56.5, 50.0)
    private var chosenBidder = "The Mask"


    private var masterTaskList: ArrayList<Task> = ArrayList()
    private var searchText = "TaskTitle"

    private var myTasksList: ArrayList<Task> = ArrayList()
    private var myTasksAdapter: TaskListAdapter = TaskListAdapter(myTasksList)

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

    @Test
    fun taskRequesterAssignedTest(){
        val userController = UserController(activity)
        username = userController.getLocalUserName()

        val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)

        GenerateRetrofit.generateRetrofit().createTask(task).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.i("network", response.body().toString())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                return
            }
        })

        Thread.sleep(2000)


        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.i("network", response.body().toString())
                masterTaskList.clear()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                myTasksList.clear()

                myTasksList.addAll(masterTaskList.filter { it ->
                    it.chosenBidder == username
                })

                if (myTasksList.size == 0) {
                    Log.e("Search Test Malfunction", myTasksList.toString())
                } else {


                    Assert.assertTrue(myTasksList.contains(task))

                    Log.d("SEARCH", "Searching statements done!")
                }

            }
            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
            }
        })

        Thread.sleep(2000)

        myTasksAdapter.notifyDataSetChanged()

        Thread.sleep(1000)

        Assert.assertEquals(myTasksView.visibility, View.VISIBLE)


        val item = myTasksView.getChildAt(0)


        if(item == null){
            Log.e("Item", "Item is null")
        }
        else{

            Assert.assertEquals(item.visibility, View.VISIBLE)

            Assert.assertEquals(item.taskTitle.visibility, View.VISIBLE)
            Assert.assertEquals(item.taskDesc.visibility, View.VISIBLE)
            Assert.assertEquals(item.taskStatus.visibility, View.VISIBLE)
            Assert.assertEquals(item.taskLowestBid.visibility, View.VISIBLE)
        }

        deleteTestTask()

    }


}
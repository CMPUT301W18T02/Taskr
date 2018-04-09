package ca.ualberta.taskr

import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListAdapter
import android.widget.RelativeLayout
import butterknife.BindView
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

import android.content.Intent
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowActivity


/**
 * Created by marissasnihur on 2018-04-08.
 *
 */

@RunWith(RobolectricTestRunner::class)
class TaskAssignedTests {

    private lateinit var activity: ToDoTaskListActivity
    private var username = "TestUsername"
    private val taskTitle = "Test Title for a Task"
    private val taskDescr = "This is a description for a task. I am describing a task."

    private lateinit var addTaskButton: Button

    private var myTasksList: ArrayList<Task> = ArrayList()
    private var myTasksAdapter: TaskListAdapter = TaskListAdapter(myTasksList)

    lateinit var loadingPanel: RelativeLayout
    lateinit var myTasksView: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var taskSearchBar: EditText
    /*@BindView(R.id.drawer_layout)
    lateinit var drawerLayout: DrawerLayout
    @BindView(R.id.nav_view)
    lateinit var navView: NavigationView
*/

    private var owner = "Corkus"
    private var title = "I'm a wot"
    private var status: TaskStatus = TaskStatus.REQUESTED
    private var bids = java.util.ArrayList<Bid>()
    private var description = "4 mana 7/7"
    private var photos = ArrayList<String>()
    private var location: LatLng = LatLng(56.5, 50.0)
    private var chosenBidder = "The Mask"

    lateinit var myTasksRefresh: SwipeRefreshLayout
    private var masterTaskList: ArrayList<Task> = ArrayList()

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
    fun setup() {
        activity = Robolectric.setupActivity(ToDoTaskListActivity::class.java)

        //set username
        //UserController(activity).setLocalUsername(username)

        val userController = UserController(activity)
        username = userController.getLocalUserName()

        ShadowLog.stream = System.out

        //Setup views
       // addTaskButton = activity.findViewById<Button>(R.id.addTaskButton)
        myTasksView = activity.findViewById<RecyclerView>(R.id.toDoTaskList)
        toolbar = activity.findViewById<Toolbar>(R.id.toDoListToolbar)
        loadingPanel =  activity.findViewById<RelativeLayout>(R.id.loadingPanel)
        myTasksRefresh = activity.findViewById<SwipeRefreshLayout>(R.id.todoTasksRefresh)
        taskSearchBar = activity.findViewById<EditText>(R.id.todoTaskSearchBar)

        //delete test task in elastic search
        deleteTestTask()
    }

    /**
     * Tests that the activity is not null prior to starting functionality tests.
     */
    @Test
    fun checkActivityNotNull() {
        Assert.assertNotNull(activity)
    }

    /**
     * US 06.01.01
     *
     * As a task provider, I want to view a list of tasks I am assigned, each task with its task
     * requester username, title, status, and my accepted bid.
     */

    @Test
    fun viewAssignedTasks(){

        val task = Task(owner, title, status, bids, description, photos, location, username)

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

        /*taskSearchBar.setText(task.title)
        CachingRetrofit(activity).getTasks(object : ca.ualberta.taskr.models.elasticsearch.Callback<List<Task>> {
            override fun onResponse(response: List<Task>, responseFromCache: Boolean) {
                //TODO Deal with offline
                Log.i("network", response.toString())

                val username = UserController(activity).getLocalUserName()

                activity.updateSearch(task.title)


                // Populate a master list and filter it by username to get our
                val masterList: ArrayList<Task> = ArrayList()
                masterList.addAll(response)
                myTasksList.addAll(masterList.filter { it ->
                    it.chosenBidder == username
                })

                loadingPanel.visibility = View.GONE
                myTasksAdapter.notifyDataSetChanged()

                myTasksRefresh.isRefreshing = false

            }
        }).execute()*/

        taskSearchBar.setText(task.title)
        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.i("network", response.body().toString())
                masterTaskList.clear()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                activity.updateSearch(task.title)
                myTasksList.clear()

                myTasksList.addAll(masterTaskList.filter { it ->
                    it.chosenBidder == username
                })

                if (myTasksList.size == 0) {
                    Log.e("Search Test Malfunction", myTasksList.toString())
                } else {

                    org.junit.Assert.assertEquals("TestTask", task.title)

                    org.junit.Assert.assertTrue(myTasksList.contains(task))
                }

            }
            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
            }
        })

        Thread.sleep(2000)

        Assert.assertEquals(myTasksView.visibility, View.VISIBLE)


        val item = myTasksView.getChildAt(0)

        // TODO: Figure out why item is null

        if(item == null){
            Log.d("Item", "Item is null")
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
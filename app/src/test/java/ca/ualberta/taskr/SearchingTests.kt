package ca.ualberta.taskr

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.EditText
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
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
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowActivity
import org.robolectric.shadows.ShadowLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by marissasnihur on 2018-03-25.
 *
 * Tests Use Cases 04.01.01 and 04.02.01
 *
 * Deals with all of the test cases involved with Searching through tasks in the ListTasksActivity
 * activity.
 *
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(26))
class SearchingTests {

    private lateinit var activity: ListTasksActivity
    private lateinit var nextActivity: ShadowActivity
    private var searchText = "TestTask"
    private var shownTaskList: ArrayList<Task> = ArrayList()

    private var owner = "TestTaskUser"
    private var title = "TestTaskTitle"

    private var masterTaskList: ArrayList<Task> = ArrayList()
    //private var taskListAdapter: TaskListAdapter = TaskListAdapter(shownTaskList)
    private lateinit var username: String

    private var status: TaskStatus = TaskStatus.REQUESTED
    private var bids = java.util.ArrayList<Bid>()
    private var description = "TestTaskDescription"
    private var photos = java.util.ArrayList<String>()
    private var location: LatLng = LatLng(65.0,66.0)
    private var chosenBidder = "The Mask"
    private lateinit var id: ElasticsearchID

    lateinit var taskList: RecyclerView

    lateinit var searchBar: EditText

    /**
     * Sets up all of the information needed by the test class.
     */

    @Before
    fun setUp(){
        activity = Robolectric.setupActivity(ListTasksActivity::class.java)

        searchBar = activity.findViewById<EditText>(R.id.taskSearchBar)
        taskList = activity.findViewById<RecyclerView>(R.id.taskList)

        val userController = UserController(activity)
        username = userController.getLocalUserName()

        ShadowLog.stream = System.out

    }
    /**
     * Delete tasks that are added to the database for the purpose of testing.
     */


    private fun deleteTestTask(){
        //delete test task in elastic search, @JamesCook
        GenerateRetrofit.generateRetrofit().getTaskID(Query.taskQuery(username, title, description))
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
     *
     * US 04.01.01, US 04.02.01
     *
     * As a task provider, I want to specify a set of keywords, and search for all tasks,
     * with status: requested or bidded, whose description contains all the keywords.
     *
     * As a task provider, I want search results to show each task with its task requester
     * username, title, status, lowest bid so far (if any).
     *
     */

    /**
     * Makes sure that the activity that is created is not null for the purpose of the Test
     * Class.
     */

    @Test
    fun checkActivityNotNull() {
        Assert.assertNotNull(activity)
    }

    /**
     * Test to check if the tasks in the database contain any of the test
     * that is entered into the searchBar.
     */

    @Test
    fun testSearchContains() {
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
        searchBar.setText(searchText)
        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.i("network", response.body().toString())
                masterTaskList.clear()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                activity.updateSearch(searchText)
                shownTaskList.clear()

                shownTaskList.addAll(masterTaskList.filter {
                    it -> (it.status != TaskStatus.ASSIGNED && it.status != TaskStatus.DONE)
                        && (it.owner != username)
                        && ((it.title != null && it.title.contains(searchText, true)) ||
                        (it.description != null && it.description.contains(searchText, true)))
                })

                if(shownTaskList.size == 0){
                    Log.e("Search Test Malfunction", shownTaskList.toString())
                }
                else {

                    Assert.assertEquals("TestTask", searchText)

                    Assert.assertTrue(shownTaskList.contains(task))
                }

            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
            }
        })

        deleteTestTask()
    }

    /**
     * US 04.02.01
     *
     * As a task provider, I want search results to show each task with its task requester
     * username, title, status, lowest bid so far (if any).
     *
     * Test to make sure that all of the information that needs to be displayed is indeed
     * displayed by the task in the application.
     */

    @Test
    fun onSearchResult(){
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
        Thread.sleep(1000)

        searchBar.setText(searchText)
        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.i("network", response.body().toString())
                masterTaskList.clear()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                activity.updateSearch(searchText)
                shownTaskList.clear()

                shownTaskList.addAll(masterTaskList.filter {
                    it -> (it.status != TaskStatus.ASSIGNED && it.status != TaskStatus.DONE)
                        && (it.owner != username)
                        && ((it.title != null && it.title.contains(searchText, true)) ||
                        (it.description != null && it.description.contains(searchText, true)))
                })

                if(shownTaskList.size == 0){
                    Log.d("Search Test Malfunction", shownTaskList.toString())
                }
                else {


                    Assert.assertEquals("TestTask", searchText)

                    val newTask = shownTaskList[0]

                    Assert.assertTrue(shownTaskList[0] == task)

                    Assert.assertEquals(newTask.owner, owner)
                    Assert.assertEquals(newTask.title, title)
                    Assert.assertEquals(newTask.status, status)
                    Assert.assertEquals(newTask.bids, bids)

                    //check to see if next activity starts


                    taskList.performClick()

                    Thread.sleep(1000)

                    val intent = Intent(activity, ListTasksActivity::class.java)

                    Assert.assertEquals(EditTaskActivity::class.java.canonicalName, intent.component.className)

                    Thread.sleep(1000)
                }

            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
            }
        })
        deleteTestTask()
    }

}
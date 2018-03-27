package ca.ualberta.taskr.models.IntegrationTests

import android.support.v7.widget.RecyclerView
import android.util.Log
import ca.ualberta.taskr.BuildConfig
import ca.ualberta.taskr.ListTasksActivity
import ca.ualberta.taskr.adapters.TaskListAdapter
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by marissasnihur on 2018-03-25.
 *
 * Tests Use Cases 04.01.01 and 04.02.01
 *
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(26))
class SearchingTests {

    private lateinit var activity: ListTasksActivity
    private var searchText = "TestTask"
    private var shownTaskList: ArrayList<Task> = ArrayList()

    private var owner = "TestTaskUser"
    private var title = "TestTaskTitle"

    private lateinit var viewManager: RecyclerView.LayoutManager

    private var masterTaskList: ArrayList<Task> = ArrayList()
    private var taskListAdapter: TaskListAdapter = TaskListAdapter(shownTaskList)
    private lateinit var username: String

    private var status: TaskStatus? = null
    private var bids = java.util.ArrayList<Bid>()
    private var description = "TestTaskDescription"
    private var photos = java.util.ArrayList<String>()
    private var location: LatLng? = null
    private var chosenBidder = "The Mask"
    private lateinit var id: ElasticsearchID

    @Before
    fun setUp(){
        activity = Robolectric.setupActivity(ListTasksActivity::class.java)

        val userController = UserController(activity)
        username = userController.getLocalUserName()

    }

    @Test
    fun checkActivityNotNull() {
        Assert.assertNotNull(activity)
    }

    /**
     * As a task provider, I want to specify a set of keywords, and search for all tasks,
     * with status: requested or bidded, whose description contains all the keywords.
     *
     */

    @Test
    fun searchTest() {
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

                Assert.assertEquals("TestText",searchText)

                Assert.assertTrue(shownTaskList.contains(task))

            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
            }
        })
    }

    /**
     * As a task provider, I want search results to show each task with its task requester
     * username, title, status, lowest bid so far (if any).
     */

    @Test
    fun searchResultsTest(){
        
    }




}
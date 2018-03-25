package ca.ualberta.taskr

import android.content.Intent
import android.widget.Button
import ca.ualberta.taskr.*
import ca.ualberta.taskr.models.User
import org.junit.Assert.*
import org.robolectric.Shadows

import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.EditText
import android.widget.RelativeLayout
import butterknife.BindView
import ca.ualberta.taskr.adapters.TaskListAdapter
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Robolectric
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
class SearchingTests {

    private lateinit var activity: ListTasksActivity
    private var searchText = "Test"
    private var shownTaskList: ArrayList<Task> = ArrayList()

    private var taskUser = "TestUser"
    private var taskTitle = "TestTaskTitle"
    private var taskStatus: TaskStatus? = null
    private var taskBids = ArrayList<Bid>()

    @BindView(R.id.taskList)
    lateinit var taskList: RecyclerView

    @BindView(R.id.drawer_layout)
    lateinit var drawerLayout: DrawerLayout

    @BindView(R.id.taskSearchBar)
    lateinit var searchBar: EditText

    @BindView(R.id.taskListToolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.loadingPanel)
    lateinit var loadingPanel: RelativeLayout

    @BindView(R.id.nav_view)
    lateinit var navView: NavigationView

    private lateinit var viewManager: RecyclerView.LayoutManager

    private var masterTaskList: ArrayList<Task> = ArrayList()
    private var taskListAdapter: TaskListAdapter = TaskListAdapter(shownTaskList)
    private lateinit var username: String

    @Before
    fun setUp(){
        activity = Robolectric.setupActivity(ListTasksActivity::class.java)
    }

    @Test
    fun checkActivityNotNull() {
        Assert.assertNotNull(activity)
    }

    /**
     * As a task provider, I want search results to show each task with its task requester username,
     * title, status, lowest bid so far (if any).
     */

    @Test
    fun searchTest() {
        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.i("network", response.body().toString())
                val masterTaskList: ArrayList<Task> = ArrayList()
                masterTaskList.clear()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                activity.updateSearch(searchText)

                shownTaskList.addAll(masterTaskList.filter {
                    it -> (it.status != TaskStatus.ASSIGNED && it.status != TaskStatus.DONE)
                        && (it.owner != username)
                        && ((it.title != null && it.title.contains(searchText, true)) ||
                        (it.description != null && it.description.contains(searchText, true)))
                })
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
            }
        })
    }



}
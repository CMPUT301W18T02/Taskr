package ca.ualberta.taskr

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar

import android.util.Log
import android.view.MenuItem
import android.view.View

import android.widget.Button
import android.widget.RelativeLayout

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.adapters.TaskListAdapter
import ca.ualberta.taskr.controllers.NavViewController
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Callback

/**
 * Displays a list of all tasks owned by the current user and a button for creating new tasks.
 * Tasks can be clicked to launch [ViewTaskActivity] for that task.
 *
 * @property loadingPanel [RelativeLayout] for loading panel.
 * @property myTasksView [RecyclerView] that displays user's tasks.
 * @property toolbar [Toolbar] for top of screen with back button.
 * @property drawerLayout [DrawerLayout] for [NavViewController]
 * @property myTasksRefresh [SwipeRefreshLayout] for refreshing user task list.
 * @property viewManager [RecyclerView.LayoutManager] for list adapter.
 * @property myTasksList List of user's task.
 * @property myTasksAdapter [TaskListAdapter] for list of user's task.
 *
 * @see [ViewTaskActivity]
 * @see [TaskListAdapter]
 * @see [RecyclerView]
 * @see [Toolbar]
 */
class MyTasksActivity : AppCompatActivity() {
    @BindView(R.id.loadingPanel)
    lateinit var loadingPanel: RelativeLayout
    @BindView(R.id.myTasksView)
    lateinit var myTasksView: RecyclerView
    @BindView(R.id.myTasksToolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.drawer_layout)
    lateinit var drawerLayout: DrawerLayout
    @BindView(R.id.nav_view)
    lateinit var navView: NavigationView
    @BindView(R.id.myTasksRefresh)
    lateinit var myTasksRefresh: SwipeRefreshLayout

    private lateinit var viewManager: RecyclerView.LayoutManager

    private var myTasksList: ArrayList<Task> = ArrayList()
    private var myTasksAdapter: TaskListAdapter = TaskListAdapter(myTasksList)

    /**
     * Initializes all views including toolbar and [RecyclerView] for task list.
     * Sets listener for task list that starts [ViewTaskActivity] for the clicked task and sets
     * the refresh listener to repopulate the task list on refresh.
     *
     * @param savedInstanceState
     * @see [RecyclerView]
     * @see [TaskListAdapter]
     * @see [Toolbar]
     * @see [SwipeRefreshLayout]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tasks)
        ButterKnife.bind(this)

        // Initialize toolbar
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu)

        // Build up recycle view
        viewManager = LinearLayoutManager(this)
        myTasksView.apply {
            layoutManager = viewManager
            adapter = myTasksAdapter
        }

        NavViewController(navView, drawerLayout, applicationContext)

        // Set listener to start activity for task details on click
        myTasksAdapter.setClickListener(View.OnClickListener {
            val position = myTasksView.getChildLayoutPosition(it)
            val viewTaskIntent = Intent(applicationContext, ViewTaskActivity::class.java)
            val bundle = Bundle()
            val strTask = GenerateRetrofit.generateGson().toJson(myTasksList[position])
            bundle.putString("TASK", strTask)
            viewTaskIntent.putExtras(bundle)
            startActivity(viewTaskIntent)
        })

        populateList()

        myTasksRefresh.setOnRefreshListener({
            populateList()
        })
    }

    /**
     * Populate the task list with the user's current active tasks.
     * All tasks on the server are obtained before being filtered
     * for those owned by the user.
     *
     * @see [CachingRetrofit]
     * @see [TaskListAdapter]
     * @see [SwipeRefreshLayout]
     */
    private fun populateList() {
        loadingPanel.visibility = View.VISIBLE
        myTasksList.clear()
        myTasksAdapter.notifyDataSetChanged()

        CachingRetrofit(this).getTasks(object : Callback<List<Task>> {
            override fun onResponse(response: List<Task>, responseFromCache: Boolean) {
                //TODO Deal with offline
                Log.i("network", response.toString())

                val username = UserController(applicationContext).getLocalUserName()

                // Populate a master list and filter it by username to get our
                val masterList: ArrayList<Task> = ArrayList()
                masterList.addAll(response)
                myTasksList.addAll(masterList.filter { it ->
                    it.owner == username
                })

                loadingPanel.visibility = View.GONE
                myTasksAdapter.notifyDataSetChanged()

                myTasksRefresh.isRefreshing = false

            }
        }).execute()
    }

    /**
     * On clicking the Add Task Button, open a blank [EditTaskActivity].
     *
     * @see [EditTaskActivity]
     * @see [TaskListAdapter]
     */
    @OnClick(R.id.addTaskButton)
    fun openEditTaskActivity() {
        val editTaskIntent = Intent(applicationContext, EditTaskActivity::class.java)
        startActivityForResult(editTaskIntent, 1)
        myTasksAdapter.notifyDataSetChanged()
    }

    /**
     * Repopulate list of users after new task is created in [EditTaskActivity].
     *
     * @see [EditTaskActivity]
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //process returned code
                populateList()
            }
        }
    }

    /**
     * @param item
     * @return [Boolean]
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Refresh list each time activity is resumed.
     */
    override fun onResume() {
        super.onResume()
        populateList()
    }
}
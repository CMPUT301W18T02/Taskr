package ca.ualberta.taskr

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.ButterKnife
import ca.ualberta.taskr.adapters.MyBidsListAdapter
import ca.ualberta.taskr.controllers.NavViewController
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.Callback
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit

/**
 * Displays a list of all tasks the current user has bidded on. Tasks are only displayed
 * if they are not ASSIGNED or DONE, and displayed tasks can be clicked to open a
 * [ViewTaskActivity] for that task.
 *
 * @property myBidsList [RecyclerView] displaying list of tasks bidded on by user.
 * @property drawerLayout [DrawerLayout] for [NavViewController]
 * @property toolbar [Toolbar] for top of screen with back button.
 * @property loadingPanel [RelativeLayout] for loading panel.
 * @property navView [NavigationView] for [NavViewController]
 * @property myBidsRefresh [SwipeRefreshLayout] for refreshing bidded task list.
 *
 * @property viewManager
 * @property masterTaskList List of all tasks on server.
 * @property shownTaskList List of tasks bidden on by user with BID status.
 * @property myBidsListAdapter [MyBidsListAdapter] for shownTaskList.
 * @property username Username of current user.
 */
class MyBidsActivity : AppCompatActivity() {

    @BindView(R.id.myBidsList)
    lateinit var myBidsList: RecyclerView
    @BindView(R.id.drawer_layout)
    lateinit var drawerLayout: DrawerLayout
    @BindView(R.id.myBidsToolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.loadingPanel)
    lateinit var loadingPanel: RelativeLayout
    @BindView(R.id.nav_view)
    lateinit var navView: NavigationView
    @BindView(R.id.myBidsRefresh)
    lateinit var myBidsRefresh: SwipeRefreshLayout

    private lateinit var viewManager: RecyclerView.LayoutManager
    private var masterTaskList: ArrayList<Task> = ArrayList()
    private var shownTaskList: ArrayList<Task> = ArrayList()
    private lateinit var myBidsListAdapter: MyBidsListAdapter
    private lateinit var username: String

    /**
     * Initializes all views including toolbar and [RecyclerView] for task list.
     * Sets listener for task list that starts [ViewTaskActivity] for the clicked task and sets
     * the refresh listener to repopulate the task list on refresh.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bids)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        var actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu)

        username = UserController(this).getLocalUserName()

        myBidsListAdapter = MyBidsListAdapter(shownTaskList, username)

        // Build up recycle view
        viewManager = LinearLayoutManager(this)
        myBidsList.apply {
            layoutManager = viewManager
            adapter = myBidsListAdapter
        }

        NavViewController(navView, drawerLayout, applicationContext)

        updateTasks()

        myBidsListAdapter.setClickListener(View.OnClickListener {
            val position = myBidsList.getChildLayoutPosition(it)
            val viewTaskIntent = Intent(applicationContext, ViewTaskActivity::class.java)
            val bundle = Bundle()
            val strTask = GenerateRetrofit.generateGson().toJson(shownTaskList[position])
            bundle.putString("TASK", strTask)
            viewTaskIntent.putExtras(bundle)
            startActivity(viewTaskIntent)
        })

        myBidsRefresh.setOnRefreshListener({
            updateTasks()
        })
    }

    /**
     * Network call to generate the master task list.
     *
     * @see [CachingRetrofit]
     */
    fun updateTasks() {
        CachingRetrofit(this).getTasks(object: Callback<List<Task>> {
            override fun onResponse(response: List<Task>, responseFromCache: Boolean) {
                //TODO Deal with offline
                masterTaskList.clear()
                masterTaskList.addAll(response as ArrayList<Task>)
                filterTasks()
                myBidsRefresh.isRefreshing = false
            }
        }).execute()
    }

    /**
     * The android built in listener for the menu button on the toolbar.
     *
     * @see [Toolbar]
     * @see [DrawerLayout]
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
     * Use to update the myBidsList by applying a filter to the master list.
     */
    fun filterTasks(){
        loadingPanel.visibility = View.VISIBLE
        shownTaskList.clear()
        myBidsListAdapter.notifyDataSetChanged()

        shownTaskList.addAll(masterTaskList.filter {
            it -> (it.status != TaskStatus.ASSIGNED && it.status != TaskStatus.DONE)
            && (it.owner != username)
            && (it.bids.count { it.owner == username } > 0)
        })
        loadingPanel.visibility = View.GONE

        myBidsListAdapter.notifyDataSetChanged()
    }

    /**
     * Refresh list each time activity is resumed.
     */
    override fun onResume() {
        super.onResume()
        updateTasks()
    }
}

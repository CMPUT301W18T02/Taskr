package ca.ualberta.taskr

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.adapters.TaskListAdapter
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import android.support.design.widget.NavigationView
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.RelativeLayout
import ca.ualberta.taskr.controllers.NavViewController
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.Callback
import ca.ualberta.taskr.util.AdHelper.Companion.isAdFree
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView


/**
 *  The master task list activity
 *
 *  @author eyesniper2
 */
class ListTasksActivity : AppCompatActivity() {

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

    @BindView(R.id.taskListRefresh)
    lateinit var taskListRefresh: SwipeRefreshLayout

    private lateinit var viewManager: RecyclerView.LayoutManager

    private var searchText: String = ""
    private var masterTaskList: ArrayList<Task> = ArrayList()
    private var shownTaskList: ArrayList<Task> = ArrayList()
    private var taskListAdapter: TaskListAdapter = TaskListAdapter(shownTaskList)
    private lateinit var username: String
    lateinit var mAdView: AdView


    /**
     * The on create method for the ListTasksActivity.
     * Will set up the toolbar, base list, setup activity listeners and kick off network requests to get data
     * from elastic search.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_tasks)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        var actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu)

        username = UserController(this).getLocalUserName()

        // Build up recycle view
        viewManager = LinearLayoutManager(this)
        taskList.apply {
            layoutManager = viewManager
            adapter = taskListAdapter
        }

        NavViewController(navView, drawerLayout, applicationContext)

        mAdView = findViewById(R.id.adView)
        if (isAdFree(this)) {
            mAdView.visibility = View.GONE
            Log.d("AD", "Visibility is GONE")
        } else {
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)
        }

        updateTasks()

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Pass
            }

            override fun beforeTextChanged(searchText: CharSequence?, start: Int, count: Int, after: Int) {
                // pass
            }

            override fun onTextChanged(localSearchText: CharSequence?, start: Int, end: Int, count: Int) {
                searchText = localSearchText.toString()
                updateSearch(searchText)
            }
        })

        taskListAdapter.setClickListener(View.OnClickListener {
            val position = taskList.getChildLayoutPosition(it)
            val viewTaskIntent = Intent(applicationContext, ViewTaskActivity::class.java)
            val bundle = Bundle()
            val strTask = GenerateRetrofit.generateGson().toJson(shownTaskList[position])
            bundle.putString("TASK", strTask)
            viewTaskIntent.putExtras(bundle)
            startActivity(viewTaskIntent)
        })

        taskListRefresh.setOnRefreshListener({
            updateTasks()
        })
    }

    /**
     * Network call to generate the master task list
     */
    private fun updateTasks() {
        CachingRetrofit(this).getTasks(object : Callback<List<Task>> {
            override fun onResponse(response: List<Task>, responseFromCache: Boolean) {
                //TODO Deal with offline
                masterTaskList.clear()
                masterTaskList.addAll(response as ArrayList<Task>)
                updateSearch(searchText)
                taskListRefresh.isRefreshing = false

            }
        }).execute()
    }

    /**
     * The android built in listener for the menu button on the toolbar
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
     * Use to update the shownTaskList by applying a search filter to the master list
     */
    fun updateSearch(textToSearch: String) {
        loadingPanel.visibility = View.VISIBLE
        shownTaskList.clear()
        taskListAdapter.notifyDataSetChanged()

        shownTaskList.addAll(masterTaskList.filter { it ->
            (it.status != TaskStatus.ASSIGNED && it.status != TaskStatus.DONE)
                    && (it.owner != username)
                    && ((it.title != null && it.title.contains(textToSearch, true)) || (it.description != null && it.description.contains(textToSearch, true)))
        })
        loadingPanel.visibility = View.GONE

        taskListAdapter.notifyDataSetChanged()
    }

    @OnClick(R.id.viewTaskMapButton)
    fun openMapView() {
        val nearbyTasksIntent = Intent(applicationContext, NearbyTasksActivity::class.java)
        startActivity(nearbyTasksIntent)
    }

    override fun onResume() {
        super.onResume()
        updateTasks()
    }

}

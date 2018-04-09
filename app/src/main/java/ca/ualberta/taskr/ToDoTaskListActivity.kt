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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import butterknife.BindView
import butterknife.ButterKnife
import ca.ualberta.taskr.adapters.TaskListAdapter
import ca.ualberta.taskr.controllers.NavViewController
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import retrofit2.Call
import retrofit2.Response
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.Callback


/**
 * Displays a list of all tasks assigned to the user (i.e. bidded tasks where the user's bid
 * was accepted). Displayed tasks can be clicked to open a [ViewTaskActivity] for that task.
 *
 * @property toDoTaskList [RecyclerView] of assigned tasks.
 * @property drawerLayout [DrawerLayout] for [NavViewController]
 * @property searchBar [EditText] for search terms.
 * @property toolbar [Toolbar] for top of screen containing back button.
 * @property loadingPanel [RelativeLayout] for loading panel.
 * @property navView [NavigationView] for [NavViewController]
 * @property todoTasksRefresh [SwipeRefreshLayout] for refreshing assigned task list.
 *
 * @property viewManager [RecyclerView.LayoutManager] for list adapter.
 * @property searchText [String] of search terms.
 * @property masterTaskList List of all tasks on server.
 * @property shownTaskList List of all tasks assigned to user.
 * @property taskListAdapter [TaskListAdapter] for shownTaskList
 * @property username [String] of current user's username.
 */
class ToDoTaskListActivity : AppCompatActivity() {

    @BindView(R.id.toDoTaskList)
    lateinit var toDoTaskList: RecyclerView
    @BindView(R.id.drawer_layout)
    lateinit var drawerLayout: DrawerLayout
    @BindView(R.id.todoTaskSearchBar)
    lateinit var searchBar: EditText
    @BindView(R.id.toDoListToolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.loadingPanel)
    lateinit var loadingPanel: RelativeLayout
    @BindView(R.id.nav_view)
    lateinit var navView: NavigationView
    @BindView(R.id.todoTasksRefresh)
    lateinit var todoTasksRefresh: SwipeRefreshLayout

    private lateinit var viewManager: RecyclerView.LayoutManager

    private var searchText: String = ""
    private var masterTaskList: ArrayList<Task> = ArrayList()
    private var shownTaskList: ArrayList<Task> = ArrayList()
    private var taskListAdapter: TaskListAdapter = TaskListAdapter(shownTaskList)
    private lateinit var username: String

    /**
     * Initializes all views including toolbar and [RecyclerView] for task list.
     * Sets listener for task list that starts [ViewTaskActivity] for the clicked task and sets
     * the refresh listener to repopulate the task list on refresh.
     *
     * @param savedInstanceState
     * @see [Toolbar]
     * @see [UserController]
     * @see [ViewManager]
     * @see [TaskListAdapter]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_task_list)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        var actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu)

        username = UserController(this).getLocalUserName()

        // Build up recycle view
        viewManager = LinearLayoutManager(this)
        toDoTaskList.apply {
            layoutManager = viewManager
            adapter = taskListAdapter
        }

        NavViewController(navView, drawerLayout, applicationContext)

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
            val position = toDoTaskList.getChildLayoutPosition(it)
            val viewTaskIntent = Intent(applicationContext, ViewTaskActivity::class.java)
            val bundle = Bundle()
            val strTask = GenerateRetrofit.generateGson().toJson(shownTaskList[position])
            bundle.putString("TASK", strTask)
            viewTaskIntent.putExtras(bundle)
            startActivity(viewTaskIntent)
        })

        todoTasksRefresh.setOnRefreshListener({
            updateTasks()
        })

    }

    /**
     * Network call to generate master task list in an async.
     *
     * @see CachingRetrofit
     */
    private fun updateTasks() {
        CachingRetrofit(this).getTasks(object: Callback<List<Task>> {
            override fun onResponse(response: List<Task>, responseFromCache: Boolean) {
                masterTaskList.clear()
                masterTaskList.addAll(response as ArrayList<Task>)
                updateSearch(searchText)
                todoTasksRefresh.isRefreshing = false
            }
        }).execute()
    }

    /**
     * The android built in listener for the menu button on the toolbar.
     *
     * @param item
     * @return [Boolean]
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
     * Use to update the shownTaskList by applying a search filter to the master list.
     *
     * @param textToSearch
     * @see TaskListAdapter
     */
    fun updateSearch(textToSearch : String){
        loadingPanel.visibility = View.VISIBLE
        shownTaskList.clear()
        taskListAdapter.notifyDataSetChanged()

        val keywords = textToSearch.split(' ')
        shownTaskList.addAll(masterTaskList.filter {
            it -> checkIfTaskShouldBeShown(it, keywords)
        })
        loadingPanel.visibility = View.GONE

        taskListAdapter.notifyDataSetChanged()
    }

    /**
     * Check to see if the [Task] should be shown.
     *
     * @param task Task to test
     * @param keywords Split keywords to check for
     *
     * @return Boolean of if the task should be shown
     */
    private fun checkIfTaskShouldBeShown(task:Task, keywords:List<String>) : Boolean{
        if (task.status != TaskStatus.ASSIGNED){
            return false
        }
        if (task.chosenBidder == null || task.chosenBidder != username){
            return false
        }
        val instancesOfKeywords = keywords.count {
            it -> (task.title != null && task.title.contains(it, true)) ||
                (task.description != null && task.description.contains(it, true))
        }
        if(instancesOfKeywords == keywords.size){
            return true
        }
        return false
    }

    /**
     * Refresh list of tasks whenever activity is resumed.
     */
    override fun onResume() {
        super.onResume()
        updateTasks()
    }

}

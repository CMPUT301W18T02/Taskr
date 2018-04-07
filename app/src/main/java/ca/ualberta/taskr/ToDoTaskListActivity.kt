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
import retrofit2.Callback
import retrofit2.Response


/**
 *
 *
 * @author ???
 *
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
     * Network call to generate master task list in a async
     */
    private fun updateTasks() {
        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.i("network", response.body().toString())
                masterTaskList.clear()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                updateSearch(searchText)
                todoTasksRefresh.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
            }
        })
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
    fun updateSearch(textToSearch : String){
        loadingPanel.visibility = View.VISIBLE
        shownTaskList.clear()
        taskListAdapter.notifyDataSetChanged()

        shownTaskList.addAll(masterTaskList.filter {
            it -> (it.status == TaskStatus.ASSIGNED)
                && (it.chosenBidder != null && it.chosenBidder == username)
                && ((it.title != null && it.title.contains(textToSearch, true)) || (it.description != null && it.description.contains(textToSearch, true)))
        })
        loadingPanel.visibility = View.GONE

        taskListAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        updateTasks()
    }

}

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
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private lateinit var viewManager: RecyclerView.LayoutManager

    private var searchText: String = ""
    private var masterTaskList: ArrayList<Task> = ArrayList()
    private var shownTaskList: ArrayList<Task> = ArrayList()
    private var taskListAdapter: TaskListAdapter = TaskListAdapter(shownTaskList)

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

        // Build up recycle view
        viewManager = LinearLayoutManager(this)
        taskList.apply {
            layoutManager = viewManager
            adapter = taskListAdapter
        }

        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.i("network", response.body().toString())
                masterTaskList.clear()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                updateSearch(searchText)
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
            }
        })

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
        shownTaskList.clear()
        shownTaskList.addAll(masterTaskList.filter {
            it -> it.title.contains(textToSearch) || it.description.contains(textToSearch)
        })
        taskListAdapter.notifyDataSetChanged()
    }

    @OnClick(R.id.viewTaskMapButton)
    fun openMapView(){
        val nearbyTasksIntent = Intent(applicationContext, ViewTaskActivity::class.java)
        startActivity(nearbyTasksIntent)
        finish()
    }
}

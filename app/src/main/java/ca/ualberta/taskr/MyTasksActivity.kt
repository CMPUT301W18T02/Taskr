package ca.ualberta.taskr

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout

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
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private lateinit var viewManager: RecyclerView.LayoutManager

    private var myTasksList: ArrayList<Task> = ArrayList()
    private var myTasksAdapter: TaskListAdapter = TaskListAdapter(myTasksList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tasks)
        ButterKnife.bind(this)

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
    }

    /**
     * Populate the task list with the user's current active tasks.
     */
    private fun populateList(){
        loadingPanel.visibility = View.VISIBLE
        myTasksList.clear()
        myTasksAdapter.notifyDataSetChanged()

        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.i("network", response.body().toString())

                val username = UserController(applicationContext).getLocalUserName()

                // Populate a master list and filter it by username to get our
                val masterList: ArrayList<Task> = ArrayList()
                masterList.addAll(response.body() as ArrayList<Task>)
                myTasksList.addAll(masterList.filter{
                    it -> it.owner == username
                })

                loadingPanel.visibility = View.GONE
                myTasksAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
            }
        })
    }

    /**
     * On clicking the Add Task Button, open a blank edit task activity.
     */
    @OnClick(R.id.addTaskButton)
    fun openEditTaskActivity(){
        val editTaskIntent = Intent(applicationContext, EditTaskActivity::class.java)
        startActivityForResult(editTaskIntent, 1)
        myTasksAdapter.notifyDataSetChanged()
    }

    /**
     * Process return from the EditTaskActivity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent:Intent){
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                //process returned code
                populateList()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        populateList()
    }
}
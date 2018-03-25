package ca.ualberta.taskr

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import android.util.Log
import android.view.View

import android.widget.Button
import android.widget.RelativeLayout

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.adapters.TaskListAdapter
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Callback

class MyTasksActivity : AppCompatActivity() {

    @BindView(R.id.addTaskButton)
    lateinit var addTaskButton: Button

    @BindView(R.id.loadingPanel)
    lateinit var loadingPanel: RelativeLayout


    @BindView(R.id.myTasksView)
    lateinit var myTasksView: RecyclerView

    private lateinit var viewManager: RecyclerView.LayoutManager

    private var myTasksList: ArrayList<Task> = ArrayList()
    private var myTasksAdapter: TaskListAdapter = TaskListAdapter(myTasksList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tasks)
        ButterKnife.bind(this)

        // Build up recycle view
        viewManager = LinearLayoutManager(this)
        myTasksView.apply {
            layoutManager = viewManager
            adapter = myTasksAdapter
        }

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
    private fun populateList() {
        loadingPanel.visibility = View.VISIBLE
        myTasksList.clear()
        myTasksAdapter.notifyDataSetChanged()

        CachingRetrofit(this).getTasks(object : Callback<List<Task>> {
            override fun onResponse(response: List<Task>, responseFromCache: Boolean) {
                //TODO Deal with offline
                Log.i("network", response.toString())

                // Grab username from SharedPreferences
                val editor = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE)
                val username = editor.getString("Username", null)

                // Populate a master list and filter it by username to get our
                val masterList: ArrayList<Task> = ArrayList()
                masterList.addAll(response)
                myTasksList.addAll(masterList.filter { it ->
                    it.owner == username
                })

                loadingPanel.visibility = View.GONE
                myTasksAdapter.notifyDataSetChanged()

            }
        }).execute()
    }

    /**
     * On clicking the Add Task Button, open a blank edit task activity.
     */
    @OnClick(R.id.addTaskButton)
    fun openEditTaskActivity() {
        val editTaskIntent = Intent(applicationContext, EditTaskActivity::class.java)
        startActivityForResult(editTaskIntent, 1)
        myTasksAdapter.notifyDataSetChanged()
    }

    /**
     * Process return from the EditTaskActivity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //process returned code
                populateList()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        populateList()
    }
}
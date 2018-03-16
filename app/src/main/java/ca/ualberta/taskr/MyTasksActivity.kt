package ca.ualberta.taskr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
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
 * The my tasks activity
 *
 * @author James Cook
 */
class MyTasksActivity : AppCompatActivity() {

    @BindView(R.id.addTaskButton)
    lateinit var addTaskButton: Button

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

        populateList()
    }

    /**
     * Populate the task list with the user's current active tasks.
     */
    private fun populateList(){
        //use a method that gets users tasks
        //populate list with returned tasks
        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.i("network", response.body().toString())
                var tempArrayList = ArrayList<Task>()
                tempArrayList.addAll(response.body() as ArrayList<Task>)
                myTasksList.addAll(tempArrayList.filter{
                    // TODO: Find out how to get username from SharedPreferences
                    it -> it.owner == ""
                })
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
        startActivity(editTaskIntent)
        finish()
    }
}

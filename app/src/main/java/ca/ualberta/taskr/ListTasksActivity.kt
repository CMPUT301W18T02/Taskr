package ca.ualberta.taskr

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import ca.ualberta.taskr.adapters.TaskListAdapter
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import org.androidannotations.annotations.ViewById
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ListTasksActivity : AppCompatActivity() {

    @ViewById
    private lateinit var taskList: RecyclerView

    private var masterTaskList: ArrayList<Task> = ArrayList()
    private var taskListAdapter: TaskListAdapter = TaskListAdapter(masterTaskList)
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_tasks)

        // Build up recycle view
        viewManager = LinearLayoutManager(this)
        taskList.apply {
            layoutManager = viewManager
            adapter = taskListAdapter
        }

        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                masterTaskList = response.body() as ArrayList<Task>
                taskListAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {

            }
        })


    }
}

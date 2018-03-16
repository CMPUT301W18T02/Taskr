package ca.ualberta.taskr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import butterknife.BindView
import ca.ualberta.taskr.models.Task
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.ViewById

/**
 * The my tasks activity
 *
 * @author James Cook
 */
class MyTasksActivity : AppCompatActivity() {

    @BindView(R.id.addTaskButton)
    lateinit var addTaskButton : Button

    @BindView(R.id.myTasksList)
    lateinit var myTasksList : RecyclerView

    private var myTasksArrayList : ArrayList<Task> = ArrayList()
    private var adapter : ArrayAdapter<Task> = ArrayAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tasks)

        populateList()
    }

    /**
     * Populate the task list with the user's current active tasks.
     */
    private fun populateList(){
        //use a method that gets users tasks
        //populate list with returned tasks
    }

    /**
     * On clicking the Add Task Button, open a blank edit task activity.
     */
    @Click(R.id.AddTaskButton)
    private fun openEditTaskActivity(v: View){
        //open a blank edit task activity
    }
}

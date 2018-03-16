package ca.ualberta.taskr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.ViewById

class MyTasksActivity : AppCompatActivity() {

    @ViewById lateinit var AddTaskButton : Button
    @ViewById lateinit var MyTasksListView : ListView

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

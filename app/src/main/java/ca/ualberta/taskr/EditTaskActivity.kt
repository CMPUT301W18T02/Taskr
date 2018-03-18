package ca.ualberta.taskr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

/**
 * EditTaskActivity.
 *
 * This Activity is responsible for allowing a task to be edited
 */
class EditTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
    }
}

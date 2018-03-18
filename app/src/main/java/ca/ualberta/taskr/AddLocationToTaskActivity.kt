package ca.ualberta.taskr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

/**
 * AddLocationToTaskActivity class
 *
 * This class contains the activity that allows for a MapBox GPS location to be added to a task
 */
class AddLocationToTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location_to_task)
    }
}

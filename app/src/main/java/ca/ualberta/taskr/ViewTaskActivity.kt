package ca.ualberta.taskr

/**
 * Created by Jacob Bakker on 3/12/2018.
 */

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewStub

class ViewTaskActivity: AppCompatActivity() {

    private var userType: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tasks)

        val stub = findViewById<ViewStub>(R.id.userTypeSpecific_views)
        stub.inflatedId = R.id.viewTasks_layout
        if (userType == 1) {
            stub.layoutResource = R.layout.provider_view_tasks
            stub.inflate()
        } else if (userType == 2) {
            stub.layoutResource = R.layout.requester_view_tasks
            stub.inflate()
        }
    }
}
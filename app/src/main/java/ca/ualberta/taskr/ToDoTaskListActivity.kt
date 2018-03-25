package ca.ualberta.taskr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import butterknife.ButterKnife

class ToDoTaskListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_task_list)
        ButterKnife.bind(this)
    }
}

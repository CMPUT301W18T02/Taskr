package ca.ualberta.taskr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit

class EditTaskActivity : AppCompatActivity() {

    @BindView(R.id.taskImageView)
    lateinit var taskImageView: ImageView

    @BindView(R.id.taskTitleEditText)
    lateinit var titleEditText: EditText

    @BindView(R.id.detailsEditText)
    lateinit var detailsEditText: EditText

    @BindView(R.id.locationEditText)
    lateinit var locationEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        ButterKnife.bind(this)

        if (intent.getStringExtra("Task") != null){
            val strTask: String = intent.getStringExtra("Task")
            val task = GenerateRetrofit.generateGson().fromJson(strTask, Task::class.java)
            fillBoxes(task)
        }
    }

    private fun fillBoxes(task: Task){
        titleEditText.setText(task.title)
        detailsEditText.setText(task.description)
        locationEditText.setText(task.location.toString())
    }


    @OnClick(R.id.getLocationButton)
    fun openLocationActivity(){
        val addLocationIntent = Intent(applicationContext, AddLocationToTaskActivity::class.java)
        startActivity(addLocationIntent)
        finish()
    }
}

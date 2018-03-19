package ca.ualberta.taskr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import com.mapbox.mapboxsdk.geometry.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * EditTaskActivity.
 *
 * This Activity is responsible for allowing a task to be edited
 */
class EditTaskActivity : AppCompatActivity() {

    @BindView(R.id.taskImageView)
    lateinit var taskImageView: ImageView

    @BindView(R.id.taskTitleEditText)
    lateinit var titleEditText: EditText

    @BindView(R.id.detailsEditText)
    lateinit var detailsEditText: EditText

    @BindView(R.id.locationEditText)
    lateinit var locationEditText: EditText

    var taskPassedIn: Boolean = false
    private lateinit var editTask: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        ButterKnife.bind(this)

        if (intent.getStringExtra("Task") != null){
            taskPassedIn = true
            val strTask: String = intent.getStringExtra("Task")
            editTask = GenerateRetrofit.generateGson().fromJson(strTask, Task::class.java)
            fillBoxes(editTask)
        }
    }

    private fun fillBoxes(task: Task){
        // TODO: populate images
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

    /**
     * On clicking postTaskButton, post the newly created task to the server
     */
    @OnClick(R.id.postTaskButton)
    fun postTask(){
        //create a new task object from fields, then post to server

        // Grab username from SharedPreferences
        val editor = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE)
        val taskOwner = editor.getString("Username", null)

        val taskTitle: String = titleEditText.text.toString()
        val taskStatus: TaskStatus = TaskStatus.REQUESTED
        val taskBids: ArrayList<Bid> = ArrayList()
        val taskDetails: String = detailsEditText.text.toString()
        val taskPhotos: ArrayList<String> = ArrayList()

        // Convert String to latlng
        val locationList: List<String> = locationEditText.text.toString().split(",")
        val lat: Double = locationList[0].toDouble()
        val lng: Double = locationList[0].toDouble()
        val taskLatLng = LatLng(lat, lng)

        val taskChosenBidder = ""

        val newTask = Task(taskOwner, taskTitle, taskStatus, taskBids, taskDetails, taskPhotos,
                taskLatLng, taskChosenBidder)

        // post newTask to servers
        // if a task has been passed in, edit its properties, otherwise post a new task
        if(taskPassedIn) {
            var id: ElasticsearchID
            GenerateRetrofit.generateRetrofit().getTaskID(Query.taskQuery(editTask.owner, editTask.title, editTask.description)).enqueue(object : Callback<ElasticsearchID> {
                override fun onResponse(call: Call<ElasticsearchID>, response: Response<ElasticsearchID>) {
                    Log.i("network", response.body().toString())
                    id = response.body() as ElasticsearchID
                    GenerateRetrofit.generateRetrofit().updateTask(id.toString(), newTask)
                }

                override fun onFailure(call: Call<ElasticsearchID>, t: Throwable) {
                    Log.e("network", "Network Failed!")
                    t.printStackTrace()
                    return
                }
            })
        }
        else{
            GenerateRetrofit.generateRetrofit().createTask(newTask)
        }

        val editTaskIntent = Intent()
        var strTask = GenerateRetrofit.generateGson().toJson(newTask)
        editTaskIntent.putExtra("Task", strTask)
        setResult(1, editTaskIntent)
        finish()
    }
}
package ca.ualberta.taskr

import android.app.Activity
import android.content.Intent
import android.media.Image
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
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import com.mapbox.mapboxsdk.geometry.LatLng
import retrofit2.Call
import ca.ualberta.taskr.models.elasticsearch.Callback
import retrofit2.Response

/**
 * Allows user to input title, description, and location for a task. The task can either
 * be a newly created one or an existing one to be edited.
 *
 * @property taskImageView ImageView for displaying task's images (if any).
 * @property titleEditText EditText for changing task title.
 * @property detailsEditText EditText for changing task details.
 * @property locationEditText EditText for displaying task location.
 * @property taskPassedIn Boolean indicating whether the task is being created (false) or edited (true).
 * @property editTask [Task] object representing the existing task to be edited (if provided).
 * @property position [LatLng] object for Task's location.
 *
 * @see [Task]
 * @see [LatLng]
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
    private var editTask: Task? = null
    private var position: LatLng? = null


    /**
     * Initializes task title, details, and location if a serialized [Task] object generated
     * by [GenerateRetrofit] is provided.
     *
     * @param savedInstanceState
     * @see [Task]
     * @see [GenerateRetrofit]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        ButterKnife.bind(this)

        if (intent.getStringExtra("Task") != null) {
            taskPassedIn = true
            val strTask: String = intent.getStringExtra("Task")

            editTask = GenerateRetrofit.generateGson().fromJson(strTask, Task::class.java)
            val task = editTask
            if (task != null) {
                position = task.location
                fillBoxes(task)
            }
        } else {
            editTask = null
        }
    }

    /**
     * Populates views for task images, title, details, and location if task
     * was provided to activity.
     *
     * @param task [Task] object whose attributes are used to populate views.
     * @see [Task]
     */
    private fun fillBoxes(task: Task) {
        // TODO: populate images
        titleEditText.setText(task.title)
        detailsEditText.setText(task.description)
        locationEditText.setText(task.location.toString())
    }


    /**
     * Starts [AddLocationToTaskActivity] to allow user to select new location for task. If
     * the task is being edited and already has a location, a serialized [LatLng] object generated
     * by [GenerateRetrofit] is passed to the activity so that the map camera starts on the location.
     *
     * @see [AddLocationToTaskActivity]
     * @see [LatLng]
     * @see [GenerateRetrofit]
     */
    @OnClick(R.id.getLocationButton)
    fun openLocationActivity() {
        val addLocationIntent = Intent(this, AddLocationToTaskActivity::class.java)
        addLocationIntent.putExtra("position", GenerateRetrofit.generateGson().toJson(position))
        val s = intent.getStringExtra("EXTRA_SESSION_ID")
        println(GenerateRetrofit.generateGson().toJson(position))
        startActivityForResult(addLocationIntent, 1)
    }

    /**
     * On clicking postTaskButton, creates the new task using inputted details and posts it
     * to the server using [CachingRetrofit]. New/modified [Task] object is passed back
     * to previous activity
     *
     * @see [CachingRetrofit]
     */
    @OnClick(R.id.postTaskButton)
    fun postTask() {
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
//        val taskLatLng = GenerateRetrofit.generateGson().fromJson(locationEditText.text.toString(), LatLng::class.java)
//        val locationList: List<String> = locationEditText.text.toString().split(",")
//        val lat: Double = locationList[0].toDouble()
//        val lng: Double = locationList[0].toDouble()
//        val taskLatLng = LatLng(lat, lng)

        val taskChosenBidder = ""

        val newTask = Task(taskOwner, taskTitle, taskStatus, taskBids, taskDetails, taskPhotos,
                position, taskChosenBidder)

        // post newTask to servers
        // if a task has been passed in, edit its properties, otherwise post a new task
        CachingRetrofit(this).updateTask(object : Callback<Boolean> {
            override fun onResponse(response: Boolean, responseFromCache: Boolean) {
                //TODO Deal with offline
                Log.i("UPLOADED?", response.toString())
            }
        }).execute(Pair(editTask, newTask))

        // Serialize new/modified Task object and pass back to ViewTaskActivity.
        val editTaskIntent = Intent()
        var strTask = GenerateRetrofit.generateGson().toJson(newTask)
        editTaskIntent.putExtra("Task", strTask)
        setResult(1, editTaskIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                position = GenerateRetrofit.generateGson().fromJson(data.getStringExtra("position"), LatLng::class.java)
                locationEditText.setText(position.toString())

            }
        }

    }
}
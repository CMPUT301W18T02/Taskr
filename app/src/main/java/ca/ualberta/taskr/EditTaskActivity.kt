package ca.ualberta.taskr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.Callback
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.util.AdHelper.Companion.isAdFree
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.mapbox.mapboxsdk.geometry.LatLng

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
 */
class EditTaskActivity : AppCompatActivity() {

    @BindView(R.id.taskTitleEditText)
    lateinit var titleEditText: EditText

    @BindView(R.id.detailsEditText)
    lateinit var detailsEditText: EditText

    @BindView(R.id.locationEditText)
    lateinit var locationEditText: EditText

    @BindView(R.id.editTaskToolbar)
    lateinit var toolbar: Toolbar

    var taskPassedIn: Boolean = false
    private var editTask: Task? = null
    private var position: LatLng? = null
    private var photos: ArrayList<String> = ArrayList()
    private val userController: UserController = UserController(this)
    private lateinit var mInterstitialAd: InterstitialAd
    private var stopAds: Boolean = false


    /**
     * Initializes task title, details, and location if a serialized [Task] object generated
     * by [GenerateRetrofit] is provided.
     *
     * @param savedInstanceState
     * @see [GenerateRetrofit]
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)


        if (!isAdFree(this)) {
            try {
                mInterstitialAd = InterstitialAd(this)
                mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
                mInterstitialAd.loadAd(AdRequest.Builder().build())

            } catch (e: Throwable) {
                Log.d("AdError", "Ads can't be tested in Robolectric")
                stopAds = true
            }
        } else {
            stopAds = true
        }
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        if (intent.getStringExtra("Task") != null) {
            taskPassedIn = true
            val strTask: String = intent.getStringExtra("Task")

            editTask = GenerateRetrofit.generateGson().fromJson(strTask, Task::class.java)
            val task = editTask
            if (task != null) {
                position = task.location
                photos = task.photos
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
     */
    private fun fillBoxes(task: Task) {
        titleEditText.setText(task.title)
        detailsEditText.setText(task.description)
        locationEditText.setText(task.location.toString())
    }


    /**
     * Starts [AddLocationToTaskActivity] to allow user to select new location for task. If
     * the task is being edited and already has a location, a LatLng] object is passed to the
     * activity so that the map camera starts on the location.
     *
     * @see [AddLocationToTaskActivity]
     * @see [GenerateRetrofit]
     */
    @OnClick(R.id.getLocationButton)
    fun openLocationActivity() {
        val addLocationIntent = Intent(this, AddLocationToTaskActivity::class.java)
        addLocationIntent.putExtra("position", GenerateRetrofit.generateGson().toJson(position))
        startActivityForResult(addLocationIntent, 1)
    }

    /**
     * On clicking postTaskButton, creates the new task using inputted details and posts it
     * to the server. New/modified [Task] object is passed back to previous activity.
     *
     * @see [CachingRetrofit]
     */
    @OnClick(R.id.postTaskButton)
    fun postTask() {
        //create a new task object from fields, then post to server
        val taskOwner = userController.getLocalUserName()

        val taskTitle: String = titleEditText.text.toString()
        val taskStatus: TaskStatus = TaskStatus.REQUESTED
        val taskBids: ArrayList<Bid> = ArrayList()
        val taskDetails: String = detailsEditText.text.toString()
        val taskPhotos: ArrayList<String> = photos

        val taskChosenBidder = ""

        if(taskTitle.length <= 5){
            ErrorDialogFragment.newInstance("Title must be greater than 5 characters")
                    .show(fragmentManager, "error")
            return
        }
        if(taskDetails.length <= 10){
            ErrorDialogFragment.newInstance("Details must be greater than 10 characters")
                    .show(fragmentManager, "error")
            return
        }


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

        if (!stopAds && mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.")
        }

        // Serialize new/modified Task object and pass back to ViewTaskActivity.
        val editTaskIntent = Intent()
        var strTask = GenerateRetrofit.generateGson().toJson(newTask)
        editTaskIntent.putExtra("Task", strTask)
        setResult(1, editTaskIntent)
        finish()
    }

    /**
     * Receives new location for task from [AddLocationToTaskActivity].
     *
     * @param requestCode
     * @param resultCode
     * @param data Contains serialized [LatLng] object for task location
     * @see [AddLocationToTaskActivity]
     * @see [GenerateRetrofit]
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                position = GenerateRetrofit.generateGson().fromJson(data.getStringExtra("position"), LatLng::class.java)
                locationEditText.setText(position.toString())
            }
        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            photos.clear()
            photos.addAll(data.getStringArrayListExtra("currentPhotos"))
        }
    }

    /**
     * Handle when the back button in [toolbar] is pressed
     *
     * @param item The menu item pressed
     * @return result
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Start an [AddPhotoToTaskActivity] for the user to add or edit photo to a task
     */
    @OnClick(R.id.taskAddImageButton)
    fun addImagesToTask() {
        val addImagesIntent = Intent(this, AddPhotoToTaskActivity::class.java)
        addImagesIntent.putStringArrayListExtra("currentPhotos", photos)
        startActivityForResult(addImagesIntent, 2)
    }
}
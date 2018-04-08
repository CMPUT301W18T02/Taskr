package ca.ualberta.taskr

/**
 * Created by Jacob Bakker on 3/12/2018.
 */

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.adapters.BidListAdapter
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.Callback
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.util.PermsUtil
import ca.ualberta.taskr.util.PhotoConversion
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import retrofit2.Call
import retrofit2.Response
import java.util.*


/**
 * Displays all information for a selected task including title, location, and bids.
 * Task Requesters can change the Task's status by either reopening or marking as done
 * an assigned task. Requesters can also accept/decline bids. Task Providers can place
 * bids with an inputted monetary amount.
 *
 * @author jtbakker
 * @property isRequester [Boolean] set to true if current user is task requester.
 * @property username Obtained from shared preferences using [UserController]
 * @property taskBidList [RecyclerView] displaying all bids on displayed task.
 * @property userController [UserController] for getting current user's username
 *
 * @property bidListAdapter [BidListAdapter] for taskBidList.
 * @property userList List of all users with bids on task. Required for [BidListAdapter].
 *
 * @property viewManager [RecyclerView.LayoutManager] for taskBidList.
 * @property displayTask [Task] object for viewed task.
 * @property oldTask
 * @property lowestBidAmount Updated with every update to task (e.g. change title, place bid).
 *
 * @property editBidFragment [EditBidFragment] for changing existing bids.
 * @property acceptBidFragment [AcceptBidFragment] for accepting/declining bids.
 * @property userInfoFragment [UserInfoFragment] containing user details for clicked username.
 * @property errorPopup [ErrorDialogFragment] displaying error message for some invalid action.
 *
 * @property taskAuthor [TextView] displaying this task's creator.
 * @property taskTitle [TextView] displaying this task's title.
 * @property taskStatus [TextView] displaying this tasks's status.
 * @property lowestBidView [TextView] displaying lowest bid amount on task.
 * @property bidListView [RecyclerView] showing all non-declined bids on task.
 * @property reopenButton [Button] for requesters to reopen task.
 * @property addOrMarkButton [Button] for adding bid if requester or marking done if provider.
 * @property editTaskButton [Button] for requester to modify task information.
 * @property toolbar [Toolbar] at top of screen. Contains back button and task title.
 * @property toolbarTitle [TextView] for displaying task title in toolbar.
 *
 * @property mapView [MapView] displaying task location.
 * @property mapboxMap [MapBoxMap] used to create displayed map.
 * @property position Current task's location as a [LatLng] object.
 * @property marker [Marker] denoting task's location on map.
 *
 * @see [UserController]
 * @see [BidListAdapter]
 * @see [EditBidFragment]
 * @see [AcceptBidFragment]
 * @see [UserInfoFragment]
 * @see [ErrorDialogFragment]
 * @see [MapBoxMap]
 * @see [UserController]
 */
class ViewTaskActivity: AppCompatActivity(), EditBidFragment.EditBidFragmentInteractionListener,
                        AcceptBidFragment.AcceptBidFragmentInteractionListener, OnMapReadyCallback,
                        MapboxMap.OnMapClickListener{

    private var isRequester: Boolean = false
    private var username : String = ""
    private var taskBidList: ArrayList<Bid> = ArrayList()
    var userController: UserController = UserController(this)

    private lateinit var bidListAdapter: BidListAdapter
    private var userList: ArrayList<User> = ArrayList()

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var displayTask: Task
    private lateinit var oldTask: Task
    private var lowestBidAmount : Double = Double.POSITIVE_INFINITY

    private lateinit var editBidFragment: EditBidFragment
    private lateinit var acceptBidFragment: AcceptBidFragment
    private lateinit var userInfoFragment: UserInfoFragment
    private lateinit var errorPopup : ErrorDialogFragment

    // Views to be modified by ViewTasksActivity
    @BindView(R.id.taskAuthorText)
    lateinit var taskAuthor: TextView
    @BindView(R.id.taskTitle)
    lateinit var taskTitle: TextView
    @BindView(R.id.taskDetails)
    lateinit var taskDetails: TextView
    @BindView(R.id.taskStatus)
    lateinit var taskStatus: TextView
    @BindView(R.id.taskPay)
    lateinit var lowestBidView: TextView
    @BindView(R.id.bidListView)
    lateinit var bidListView:RecyclerView
    @BindView(R.id.reopenButton)
    lateinit var reopenButton : Button
    @BindView(R.id.addBidOrMarkDone)
    lateinit var addOrMarkButton : Button
    @BindView(R.id.editTaskButton)
    lateinit var editTaskButton : Button
    @BindView(R.id.viewTaskToolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.viewTaskToolbarTitle)
    lateinit var toolbarTitle: TextView
    @BindView(R.id.taskBannerImage)
    lateinit var taskBannerImage: ImageView

    // Mapbox-related attributes
    @BindView(R.id.taskMapView)
    lateinit var mapView : MapView
    private lateinit var mapboxMap : MapboxMap
    private var position : LatLng? = null
    private lateinit var marker: Marker


    /**
     * Initializes the MapBox mapview, obtains the displayTask, populates and displays list of all
     * bids on tasks, and reformats ViewTaskActivity layout according to user type.
     * Task to be displayed is obtained as a serialized [Task] object.
     * If requester, displays buttons for editing, reopening, or marking done the task. If provider
     * only a button for adding bids is displaying.
     *
     * @param savedInstanceState
     * @see [MapBoxMap]
     * @see [GenerateRetrofit]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiYmFybmFidXN0aGViZW5pZ24iLCJhIjoiY2pldWI2MHN2NGhrZDJxbWU4dHdubmwxYSJ9.ZVq95tHTxTgyyppAfj3Jdw")
        setContentView(R.layout.activity_view_tasks)
        PermsUtil.getPermissions(this@ViewTaskActivity)
        ButterKnife.bind(this)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Obtain Task to be displayed.
        if (intent != null) {
            val taskStr = intent.getStringExtra("TASK")
            if (taskStr != null) {
                displayTask = GenerateRetrofit.generateGson().fromJson(taskStr, Task::class.java)
                oldTask = displayTask
                populateBidList() // Populate Bid list to be displayed.

                //Update displayed attributes for Task
                updateDetails()
                updateLowestBidAmount()
            }
        }

        // Obtain user type and reformat display accordingly.
        getUserType()
        if (isRequester) {
            addOrMarkButton.text = getResources().getString(R.string.activity_view_tasks_provider_done)
            reopenButton.visibility = View.VISIBLE
            editTaskButton.visibility = View.VISIBLE
        }

        // Initialize toolbar for back button and task title.
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        // Build up RecyclerView
        viewManager = LinearLayoutManager(this)
        createBidAdapter()

    }

    /**
     * Set listener for rows in Bid list. If user is a Task Requester, allows user to
     * select Bid and accept/decline it using [AcceptBidFragment]. If user is a Task
     * Provider and the clicked bid is their's, allows user to edit bid amount using
     * [EditBidFragment]. If task is assigned or done, neither fragment will be started
     * since bid details cannot be modified at that time.
     * If the [TextView] displaying the bidder's name is clicked, opens a [UserInfoFragment]
     * displaying that the bid owner's information instead.
     * [BidListAdapter] requires a list of all users with bids on the task to properly
     * display user profile images.
     *
     * @see [BidListAdapter]
     * @see [AcceptBidFragment]
     * @see [EditBidFragment]
     * @see [UserInfoFragment]
     * @see [GenerateRetrofit]
     */
    private fun createBidAdapter() {
        bidListAdapter = BidListAdapter(taskBidList, userList)
        bidListAdapter.setOnItemClickListener(object : BidListAdapter.OnItemClickListener {
            override fun onItemClick(view : View, position : Int) {
                val bid = taskBidList[position]
                if (view.id == R.id.bidderName) {
                    startUserInfoFragment(bid.owner)
                } else if (displayTask.status == TaskStatus.BID) {
                    if (isRequester) {
                        startAcceptBidFragment(bid)
                    } else if (username == bid.owner) {
                        startEditBidFragment(bid)
                    }
                }
            }
        })
        // Set adapter for bid list.
        bidListView.apply {
            layoutManager = viewManager
            adapter = bidListAdapter
        }
        // Get all users from server, then pass them as a list to adapter.
        GenerateRetrofit.generateRetrofit().getUsers().enqueue(object : retrofit2.Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                Log.i("network", response.body().toString())
                userList.addAll(response.body() as ArrayList<User>)
                bidListAdapter.notifyDataSetChanged()
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                return
            }
        })
    }

    /**
     * Repopulates list of bids on task.
     * If task's status is BID, all non-declined bids are added to the displayed list. If the task's
     * status is ASSIGNED or DONE, only the accepted bid is added.
     * Bids are sorted in increasing order of their amount using a [Comparator].
     */
    private fun populateBidList() {
        taskBidList.clear()
        // Task status is BID iff no bid is accepted.
        if (displayTask.status == TaskStatus.BID) {
            for (bid in displayTask.bids) {
                if (!bid.isDismissed) {taskBidList.add(bid)}
            }
        // If ASSIGNED or DONE, filter out non-accepted bids.
        } else if (displayTask.status != TaskStatus.REQUESTED){
            var chosenBidFilter = displayTask.bids.filter {b ->
                (b.owner == displayTask.chosenBidder) &&
                (b.isDismissed == false)}
            if (chosenBidFilter.isNotEmpty()) {
                var chosenBid = chosenBidFilter[0]
                taskBidList.add(chosenBid)
            }
        }
        // Sort bids in increasing order of their amount.
        taskBidList.sortWith(Comparator { bid1, bid2 ->
            when {
                bid1.amount < bid2.amount -> -1
                bid1.amount > bid2.amount -> 1
                else -> 0
            }
        })
    }

    /**
     * Starts [UserInfoFragment] containing user information corresponding to a clicked username
     * in the activity.
     *
     * @param username The clicked username
     * @see [CachingRetrofit]
     * @see [UserInfoFragment]
     * @see [GenerateRetrofit]
     */
    private fun startUserInfoFragment(username: String) {
        // Get User object from ElasticSearch index.
        CachingRetrofit(this).getUsers(object: Callback<List<User>> {
            override fun onResponse(response: List<User>, responseFromCache : Boolean) {
                var user = response.filter {u -> (u.username == username)}[0]
                var args = Bundle()
                var userStr = GenerateRetrofit.generateGson().toJson(user, User::class.java)
                args.putString("USER", userStr)

                userInfoFragment = UserInfoFragment()
                userInfoFragment.arguments = args
                userInfoFragment.show(fragmentManager, "DialogFragment")
            }
        }).execute()
    }
	
    /**
     * Displays [EditBidFragment] to allow Task Provider to update their selected bid.
     *
     * @param bid The selected bid.
     * @see [EditBidFragment]
     */
    private fun startEditBidFragment(bid : Bid?) {
        editBidFragment = EditBidFragment()
        if (bid != null) {
            var args = Bundle()
            var bidStr = GenerateRetrofit.generateGson().toJson(bid, Bid::class.java)
            args.putString("DISPLAYBID", bidStr)
            editBidFragment.arguments = args
        }
        editBidFragment.show(fragmentManager, "DialogFragment")
    }
	
    /**
     * Displays [AcceptBidFragment] to allow Task Requester to accept/decline a selected Bid.
     *
     * @param bid The selected bid.
     * @see [AcceptBidFragment]
     */
    private fun startAcceptBidFragment(bid : Bid) {
        acceptBidFragment = AcceptBidFragment()
        var args = Bundle()
        var bidStr = GenerateRetrofit.generateGson().toJson(bid, Bid::class.java)
        args.putString("DISPLAYBID", bidStr)
        acceptBidFragment.arguments = args
        acceptBidFragment.show(fragmentManager, "DialogFragment")
    }

    /**
     * Gets username from shared preferences using [UserController], then determines user type.
     * If the user is the owner of the displayed Task, then the user is a Task Requester in this
     * context. Otherwise, the user is a Task Provider.
     *
     * @see [UserController]
     */
    private fun getUserType() {
        username = userController.getLocalUserName()
        if (username == displayTask.owner) {
            isRequester = true
        }
    }

    /**
     * Updates displayed author, title, details, and status for Task.
     */
    private fun updateDetails() {
        taskAuthor.text = displayTask.owner
        taskTitle.text = displayTask.title
        toolbarTitle.text = displayTask.title
        taskDetails.text = displayTask.description
        taskStatus.text = displayTask.status?.name
        if (displayTask.photos.size != 0){
            taskBannerImage.setImageBitmap(PhotoConversion.getBitmapFromString(displayTask.photos[0]))
        }
    }

    /**
     * Implemented method for stub in interface of [EditBidFragment]. Updates
     * selected [Bid] with modified amount, then updates Task on server.
     *
     * @param bidAmount The new bid amount.
     * @param originalBid The previous bid before changes.
     * @see [EditBidFragment]
     */
    override fun bidUpdate(bidAmount : Double, originalBid : Bid) {
        var index: Int = displayTask.bids.indexOf(originalBid)
        var changedBid = Bid(displayTask.bids[index].owner, bidAmount, false)
        displayTask.setBidAtIndex(changedBid, index)
        updateDisplayTask()
    }

    /**
     * Implemented method for stub in interface of [EditBidFragment]. Adds created
     * Bid to displayed Task's list of Bids, then updates Task on server.
     *
     * @param bidAmount The new bid amount.
     * @see [EditBidFragment]
     */
    override fun bidAdd(bidAmount : Double) {
        var newBid = Bid(username, bidAmount, false)
        var existingBid = displayTask.bids.filter {u->(u.owner == username)}
        if (existingBid.isNotEmpty()) {
            var existingIndex = displayTask.bids.indexOf(existingBid[0])
            displayTask.bids[existingIndex] = newBid
        } else {
            displayTask.addBid(newBid)
            if (displayTask.status == TaskStatus.REQUESTED) {
                displayTask.status = TaskStatus.BID
            }
        }
        updateDisplayTask()
    }

    /**
     * Sets a selected [Bid] to declined, then updates the task on server.
     *
     * @param bid The bid to be declined.
     */
    override fun declinedBid(bid: Bid) {
        var changedBid = Bid(bid.owner, bid.amount, true)
        var i = displayTask.bids.indexOf(bid)
        displayTask.bids[i] = changedBid
        updateDisplayTask()
    }

    /**
     * Implemented method for stub in interface of [AcceptBidFragment]. If Task' status is not DONE
     * or ASSIGNED, updates both Task's chosen bidder to selected Bid's owner and Task's status to
     * ASSIGNED before updating Task in ElasticSearch index. Task is then updated on server.
     *
     * @param bid The [Bid] to be accepted.
     * @see [AcceptBidFragment]
     */
    override fun acceptedBid(bid: Bid) {
        if (displayTask.status != TaskStatus.ASSIGNED && displayTask.status != TaskStatus.DONE) {
            displayTask.chosenBidder = bid.owner
            displayTask.status = TaskStatus.ASSIGNED
            updateDisplayTask()
        }
    }

    /**
     * Opens [UserInfoFragment] displaying task author's information when the task author's name
     * is clicked.
     *
     * @see [UserInfoFragment]
     */
    @OnClick(R.id.taskAuthorText)
    fun taskAuthorClick() {
        startUserInfoFragment(taskAuthor.text.toString())
    }

    /**
     * This method will either set the displayed Task's status to DONE if user is its Requester or
     * allow user to add a bid to the Task otherwise using [EditBidFragment].
     * If the user is a Requester trying to mark the task done, the task status will be changed iff
     * the task is currently ASSIGNED. Otherwise, an [ErrorDialogFragment] is shown.
     * If the user is a provider trying to add a bid, the [EditBidFragment] will be opened iff the
     * task is open for bids (i.e. status is REQUESTED or BID). Otherwise, an [ErrorDialogFragment]
     * is shown.
     *
     * @param view
     * @see [EditBidFragment]
     * @see [ErrorDialogFragment]
     */
    @OnClick(R.id.addBidOrMarkDone)
    fun addBidOrMarkDone(view : View) {
        if (isRequester) {
            if (displayTask.status == TaskStatus.ASSIGNED) {
                displayTask.status = TaskStatus.DONE
                updateDisplayTask()
            } else {
                showErrorDialog(R.string.activity_view_tasks_error_mark_done)
            }
        } else if (!isRequester){
            if (displayTask.status == TaskStatus.REQUESTED || displayTask.status == TaskStatus.BID) {
                startEditBidFragment(null)
            } else {
                showErrorDialog(R.string.activity_view_tasks_error_add_bid)
            }
        }
    }

    /**
     * If displayed Task's status is ASSIGNED, update it to REQUESTED if it has no bids or
     * BID otherwise. Remove Task's chosen bidder since Task is no longer assigned Once changes
     * have been made, updates Task in ElasticSearch index.
     * If the displayed Task's status is not ASSIGNED, show an [ErrorDialogFragment] instead.
     *
     * @param view
     * @see [ErrorDialogFragment]
     */
    @OnClick(R.id.reopenButton)
    fun reopen(view: View) {
        if (displayTask.status == TaskStatus.ASSIGNED) {
            if (displayTask.bids.size == 0) {
                displayTask.status = TaskStatus.REQUESTED
            } else {
                displayTask.status = TaskStatus.BID
            }
            displayTask.chosenBidder = ""
            updateDisplayTask()
        } else {
            showErrorDialog(R.string.activity_view_tasks_error_reopen)
        }
    }

    /**
     * Starts [EditTaskActivity] to allow requester to change their task's information and if the
     * task has no bids (i.e. status is REQUESTED).
     * If the status is not REQUESTED, display an [ErrorDialogFragment] instead.
     *
     * @see [EditTaskActivity]
     * @see [ErrorDialogFragment]
     * @see [GenerateRetrofit]
     */
    @OnClick(R.id.editTaskButton)
    fun editTask() {
        if (displayTask.status == TaskStatus.REQUESTED) {
            var editTaskIntent = Intent(this, EditTaskActivity::class.java)
            var editTaskBundle = Bundle()
            var strTask = GenerateRetrofit.generateGson().toJson(displayTask)
            editTaskBundle.putString("Task", strTask)
            editTaskIntent.putExtras(editTaskBundle)
            startActivityForResult(editTaskIntent, 0)
        } else {
            showErrorDialog(R.string.activity_view_tasks_error_edit)
        }
    }

    /**
     * Executed when [EditTaskActivity] is finished. If the activity finished successfully, the
     * modified task is returned.
     *
     * @param requestCode
     * @param resultCode
     * @param data Contains a string representing the modified task.
     * @see [EditTaskActivity]
     * @see [GenerateRetrofit]]
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 1) {
            var displayTaskStr = data?.extras?.getString("Task")
            displayTask = GenerateRetrofit.generateGson().fromJson(displayTaskStr, Task::class.java)
            updateDisplayTask()
        }
    }

    /**
     * Updates displayed Task on server using [CachingRetrofit], then updates every task detail
     * being displayed in activity.
     *
     * @see [CachingRetrofit]
     */
    private fun updateDisplayTask() {
        CachingRetrofit(this).updateTask(object: Callback<Boolean> {
            override fun onResponse(response: Boolean, responseFromCache: Boolean) {
                Log.e("network", "Posted!")
            }
        }).execute(Pair(oldTask, displayTask))
        oldTask = displayTask

        // Reobtain list of Task's bids, then update RecyclerView.
        populateBidList()
        createBidAdapter()
		
        // Update remaining Task attributes in activity.
        updateDetails()
        updateLowestBidAmount()
        updateLocationInfo()
    }

    /**
     * Find lowest bid amount in Task's list of bids, then update displayed lowest bid amount.
     */
    private fun updateLowestBidAmount() {
        if (taskBidList.size == 0) {
            lowestBidView.text = ""
            return
        }
        lowestBidAmount = Double.POSITIVE_INFINITY
        for (bid : Bid in taskBidList) {
            if (lowestBidAmount > bid.amount) {
                lowestBidAmount = bid.amount
            }
        }
        lowestBidView.text = String.format(getString(R.string.activity_view_tasks_lowest_bid), lowestBidAmount)
    }

    /**
     * Default onStart() method with corresponding mapView method added.
     */
    public override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    /**
     * Default onResume() method with corresponding mapView method added.
     */
    public override fun onResume() {
        super.onResume()
        mapView.onResume()

    }
    /**
     * Default onPause method with corresponding mapView method added.
     */
    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    /**
     * Default onStop method with corresponding mapView method added.
     */
    public override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
    /**
     * Default onLowMemory method with corresponding mapView method added.
     */
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    /**
     * Default onDestroy() method with corresponding mapView method added.
     */
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
    /**
     * Default onSaveInstanceState() method with corresponding mapView method added.
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState!!)
    }

    /**
     * Initializes mapBoxMap attribute properties.
     */
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.addOnMapClickListener(this)

        updateLocationInfo()
    }

    /**
     * Sets mapView camera position to geolocation data in displayed Task. If no such data
     * is available, hide mapView instead.
     *
     * @see [MapBoxMap]
     */
    private fun updateLocationInfo() {
        if (displayTask.location != null) {
            mapView.visibility = View.VISIBLE
            try {
                marker.remove()
            } catch (e : Exception) {
                Log.i("No marker exists", "Creating new one...")
            }
            position = displayTask.location as LatLng
            marker = mapboxMap.addMarker(MarkerOptions().position(position))
            var cameraPosition : CameraPosition = CameraPosition.Builder()
                    .target(position)
                    .build()
            var camera : CameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
            mapboxMap.animateCamera(camera)
        } else {
            mapView.visibility = View.GONE
        }
    }

    /**
     * Process button presses from the tool bar.
     *
     * @param item
     * @return [Boolean]
     * @see [Toolbar]
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
     * On mapview click, shows task location in Google Maps.
     * Adapted from https://stackoverflow.com/questions/42677389/android-how-to-pass-lat-long-route-info-to-google-maps-app/45554195#45554195
     *
     * @param point
     */
    override fun onMapClick(point : LatLng) {
        var mapsURL = "http://www.google.ca/maps/dir/?api=1&destination=" + point.latitude +
                        "," + point.longitude
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsURL))
        startActivity(intent)
    }

    /**
     * Starts an [ErrorDialogFragment] displaying an error message with ID = messageID.
     *
     * @param messageID The string ID of the error message.
     * @see [ErrorDialogFragment]
     */
    private fun showErrorDialog(messageID : Int) {
        var message = getString(messageID)
        errorPopup = ErrorDialogFragment()
        var args = Bundle()
        args.putString("MESSAGE", message)
        errorPopup.arguments = args
        errorPopup.show(fragmentManager, "DialogFragment")
    }
}
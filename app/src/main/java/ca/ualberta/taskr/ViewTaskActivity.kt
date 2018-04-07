package ca.ualberta.taskr

/**
 * Created by Jacob Bakker on 3/12/2018.
 */


import android.app.Activity
import android.content.Intent
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


/**
 * ViewTaskActivity class
 *
 * This activity displays all details for a selected Task while allowing User to modify certain
 * Task attributes based on their user type.
 */
class ViewTaskActivity: AppCompatActivity(), EditBidFragment.EditBidFragmentInteractionListener,
                        AcceptBidFragment.AcceptBidFragmentInteractionListener, OnMapReadyCallback,
                        MapboxMap.OnMapClickListener{

    private var isRequester: Boolean = false
    private var username : String = ""
    private var taskBidList: ArrayList<Bid> = ArrayList()
    private var userList: ArrayList<User> = ArrayList()
    private var bidListAdapter: BidListAdapter = BidListAdapter(taskBidList, userList)
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var displayTask: Task
    private lateinit var editBidFragment: EditBidFragment
    private lateinit var acceptBidFragment: AcceptBidFragment
    private var lowestBidAmount : Double = Double.POSITIVE_INFINITY
    private lateinit var oldTask: Task


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
    private lateinit var position : LatLng
    private lateinit var marker: Marker


    /**
     * Initializes the Mapbox mapview, obtains the displayTask, populates and displays list of all
     * bids on tasks, and reformats ViewTaskActivity layout according to user type.
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
                taskBidList.addAll(displayTask.bids) // Populate Bid list to be displayed.

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

        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        // Build up RecyclerView
        viewManager = LinearLayoutManager(this)
        /**
         * Set listener for rows in Bid list. If user is a Task Requester, allows user to
         * select Bid and accept/decline it using AcceptBidFragment.
         */
        bidListAdapter.setOnItemClickListener(object : BidListAdapter.OnItemClickListener {
            override fun onItemClick(view : View, position : Int) {
                val bid = taskBidList[position]
                if (isRequester) {
                    startAcceptBidFragment(bid)
                } else if (username == bid.owner) {
                    startEditBidFragment(bid)
                }
            }
        })
        bidListView.apply {
            layoutManager = viewManager
            adapter = bidListAdapter
        }

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
     * Displays pop-up fragment that allows Task Provider to update their selected bid.
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
     * Displays pop-up fragment that allows Task Requester to accept/decline a selected Bid.
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
     * Determines user type. If the user is the owner of the displayed Task, then the user is
     * a Task Requester in this context. Otherwise, the user is a Task Provider.
     */
    private fun getUserType() {
        var editor = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE)
        username = editor.getString("Username", null)
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
     * Implemented method for stub in EditBidFragmentInteractionListener interface. Updates
     * selected Bid with modified amount, then updates Task in ElasticSearch index.
     */
    //TODO replace isDismissed with actual functionality
    override fun bidUpdate(bidAmount : Double, originalBid : Bid) {
        var index: Int = displayTask.bids.indexOf(originalBid)
        var changedBid = Bid(displayTask.bids[index].owner, bidAmount, false)
        displayTask.setBidAtIndex(changedBid, index)
        updateDisplayTask()
    }

    /**
     * Implemented method for stub in EditBidFragmentInteractionListener interface. Adds created
     * Bid to displayed Task's list of Bids, then updates Task in ElasticSearch index.
     */
    //TODO actually implement declined tasks
    override fun bidAdd(bidAmount : Double) {
        var newBid = Bid(username, bidAmount, false)
        displayTask.addBid(newBid)
        updateDisplayTask()

    }

    //TODO: Implement method for declining/removing selected Bid.
    override fun declinedBid(bid: Bid) {
        Log.i("Hit", "Decline")
    }

    /**
     * Implemented method for stub in AcceptBidFragmentInteractionListener interface. If Task's
     * status is not DONE or ASSIGNED, updates both Task's chosen bidder to selected Bid's owner
     * and Task's status to ASSIGNED before updating Task in ElasticSearch index.
     */
    override fun acceptedBid(bid: Bid) {
        if (displayTask.status != TaskStatus.ASSIGNED && displayTask.status != TaskStatus.DONE) {
            displayTask.chosenBidder = bid.owner
            displayTask.status = TaskStatus.ASSIGNED
            updateDisplayTask()
        }
    }

    /**
     * This method will either set the displayed Task's status to DONE if user is its Requester or
     * allow user to add a bid to the Task otherwise using EditBidFragment.
     */
    @OnClick(R.id.addBidOrMarkDone)
    fun addBidOrMarkDone(view : View) {
        if (isRequester && displayTask.status == TaskStatus.ASSIGNED) {
            displayTask.status = TaskStatus.DONE
            updateDisplayTask()
        } else if (!isRequester){
            startEditBidFragment(null)
        }
    }

    /**
     * If displayed Task's status is ASSIGNED, update it to REQUESTED if it has no bids or
     * BID otherwise. Remove Task's chosen bidder since Task is no longer assigned.
     * One changes have been made, updates Task in ElasticSearch index.
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
        }
    }

    @OnClick(R.id.editTaskButton)
    fun editTask() {
        var editTaskIntent = Intent(this, EditTaskActivity::class.java)
        var editTaskBundle = Bundle()
        var strTask = GenerateRetrofit.generateGson().toJson(displayTask)
        editTaskBundle.putString("Task", strTask)
        editTaskIntent.putExtras(editTaskBundle)
        startActivityForResult(editTaskIntent, Activity.RESULT_OK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            var displayTaskStr = data?.extras?.getString("Task")
            displayTask = GenerateRetrofit.generateGson().fromJson(displayTaskStr, Task::class.java)
            updateDetails()
            updateLowestBidAmount()
            updateLocationInfo()

        }
    }

    /**
     * Updates displayed Task in the ElasticSearch index, then updates every Task detail being
     * displayed in activity.
     */
    private fun updateDisplayTask() {
        CachingRetrofit(this).updateTask(object: Callback<Boolean> {
            override fun onResponse(response: Boolean, responseFromCache: Boolean) {
                Log.e("network", "Posted!")
            }
        }).execute(Pair(oldTask, displayTask))
        oldTask = displayTask

        // Reobtain list of Task's bids, then update RecyclerView.
        taskBidList.clear()
        taskBidList.addAll(displayTask.bids)
        bidListAdapter.notifyDataSetChanged()
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
        for (bid : Bid in taskBidList) {
            if (lowestBidAmount > bid.amount) {
                lowestBidAmount = bid.amount
            }
        }
        lowestBidView.text = String.format(getString(R.string.row_bid_amount), lowestBidAmount)
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
     */
    private fun updateLocationInfo() {
        if (displayTask.location != null) {
            mapView.visibility = View.VISIBLE
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
     * Process button presses from the tool bar
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

    //TODO: When map is clicked, open GoogleMaps for Task's location
    override fun onMapClick(point : LatLng) {
        Log.i("Hello", position.toString())
    }
}
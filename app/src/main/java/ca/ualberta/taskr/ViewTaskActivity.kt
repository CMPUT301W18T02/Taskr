package ca.ualberta.taskr

/**
 * Created by Jacob Bakker on 3/12/2018.
 */


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import butterknife.*
import ca.ualberta.taskr.Perms.PermsUtil
import ca.ualberta.taskr.adapters.BidListAdapter
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
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
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback

class ViewTaskActivity: AppCompatActivity(), EditBidFragment.OnFragmentInteractionListener,
                        AcceptBidFragment.OnFragmentInteractionListener, OnMapReadyCallback,
                        MapboxMap.OnMapClickListener{

    private var isRequester: Boolean = false
    private var username : String = ""
    private var taskBidList: ArrayList<Bid> = ArrayList()
    private var bidListAdapter: BidListAdapter = BidListAdapter(taskBidList)
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var displayTask: Task
    private lateinit var editBidFragment: EditBidFragment
    private lateinit var acceptBidFragment: AcceptBidFragment
    private var lowestBidAmount : Double = Double.POSITIVE_INFINITY

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
    @BindView(R.id.taskMapView)
    lateinit var mapView : MapView
    private lateinit var mapboxMap : MapboxMap
    private lateinit var position : LatLng
    private lateinit var marker: Marker
    @BindView(R.id.viewTasks_scrollView)
    lateinit var taskScrollView : ScrollView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiYmFybmFidXN0aGViZW5pZ24iLCJhIjoiY2pldWI2MHN2NGhrZDJxbWU4dHdubmwxYSJ9.ZVq95tHTxTgyyppAfj3Jdw")
        setContentView(R.layout.activity_view_tasks)
        PermsUtil.getPermissions(this@ViewTaskActivity)
        ButterKnife.bind(this)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        if (intent != null) {
            val taskStr = intent.getStringExtra("TASK")
            if (taskStr != null) {
                displayTask = GenerateRetrofit.generateGson().fromJson(taskStr, Task::class.java)
                updateDetails()
                taskBidList.addAll(displayTask.bids)
                updateBidAmount()
            }
        }
        updateDetails()
        getUserType()
        if (isRequester) {
            addOrMarkButton.text = getResources().getString(R.string.activity_view_tasks_provider_done)
            reopenButton.visibility = View.VISIBLE
        }

        viewManager = LinearLayoutManager(this)
        bidListAdapter.setOnItemClickListener(object : BidListAdapter.OnItemClickListener {
            override fun onItemClick(view : View, position : Int) {
                if (isRequester) {
                    val bid = taskBidList[position]
                    startAcceptBidFragment(bid)
                }
            }
        })
        bidListView.apply {
            layoutManager = viewManager
            adapter = bidListAdapter
        }

    }

    private fun startAcceptBidFragment(bid : Bid) {
        acceptBidFragment = AcceptBidFragment()
        var args = Bundle()
        var bidStr = GenerateRetrofit.generateGson().toJson(bid, Bid::class.java)
        args.putString("DISPLAYBID", bidStr)
        acceptBidFragment.arguments = args
        acceptBidFragment.show(fragmentManager, "DialogFragment")
    }

    private fun getUserType() {
        var editor = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE)
        username = editor.getString("Username", null)
        if (username == displayTask.owner) {
            isRequester = true
        }
    }

    private fun updateDetails() {
        taskAuthor.text = displayTask.owner
        taskTitle.text = displayTask.title
        taskDetails.text = displayTask.description
        taskStatus.text = displayTask.status?.name
    }

    override fun bidUpdate(bidAmount : Double, originalBid : Bid) {
        var index: Int = displayTask.bids.indexOf(originalBid)
        var changedBid = Bid(displayTask.bids[index].owner, bidAmount)
        displayTask.bids[index] = changedBid
        updateDisplayTask()
    }

    override fun bidAdd(bidAmount : Double) {
        var newBid = Bid(username, bidAmount)
        displayTask.bids.add(newBid)
        updateDisplayTask()

    }

    override fun declinedBid(bid: Bid) {
        Log.i("Hit", "Decline")
    }

    override fun acceptedBid(bid: Bid) {
        displayTask.chosenBidder = bid.owner
        displayTask.status = TaskStatus.ASSIGNED
        updateDisplayTask()
    }

    @OnClick(R.id.addBidOrMarkDone)
    fun addBid(view : View) {
        if (isRequester) {
            displayTask.status = TaskStatus.DONE
            updateDisplayTask()
        } else {
            editBidFragment = EditBidFragment()
            var args = Bundle()
            editBidFragment.arguments = args
            editBidFragment.show(fragmentManager, "DialogFragment")
        }
    }

    @OnClick(R.id.reopenButton)
    fun reopen(view: View) {
        if (displayTask.bids.size == 0) {
            displayTask.status = TaskStatus.REQUESTED
        } else {
            displayTask.status = TaskStatus.BID
        }
        updateDisplayTask()
    }

    private fun updateDisplayTask() {
        lateinit var id: ElasticsearchID
        GenerateRetrofit.generateRetrofit().getTaskID(Query.taskQuery(displayTask.owner, displayTask.title, displayTask.description)).enqueue(object : Callback<ElasticsearchID> {
            override fun onResponse(call: Call<ElasticsearchID>, response: Response<ElasticsearchID>) {
                Log.i("network", response.body().toString())
                id = response.body() as ElasticsearchID
                GenerateRetrofit.generateRetrofit().updateTask(id.toString(), displayTask)
            }

            override fun onFailure(call: Call<ElasticsearchID>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                return
            }
        })
        taskBidList.clear()
        taskBidList.addAll(displayTask.bids)
        bidListAdapter.notifyDataSetChanged()
        updateDetails()
        updateBidAmount()
        updateLocationInfo()
    }

    private fun updateBidAmount() {
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

    public override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    public override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    public override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState!!)
    }
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.addOnMapClickListener(this)
        updateLocationInfo()
    }

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

    override fun onMapClick(point : LatLng) {
        Log.i("Hello", position.toString())
    }
}
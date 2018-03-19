package ca.ualberta.taskr

/**
 * Created by Jacob Bakker on 3/12/2018.
 */

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.constraint.ConstraintSet
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.telecom.Call
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.*
import ca.ualberta.taskr.R.attr.layoutManager
import ca.ualberta.taskr.adapters.BidListAdapter
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import kotlinx.android.synthetic.main.activity_view_tasks.*
import org.jetbrains.annotations.Nullable
import javax.security.auth.callback.Callback

class ViewTaskActivity: AppCompatActivity(), EditBidFragment.OnFragmentInteractionListener,
                        AcceptBidFragment.OnFragmentInteractionListener{

    private var isRequester: Boolean = false
    private var username : String = ""
    private var taskBidList: ArrayList<Bid> = ArrayList()
    private var bidListAdapter: BidListAdapter = BidListAdapter(taskBidList)
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var displayTask: Task? = null
    private var editBidFragment: EditBidFragment? = null
    private var acceptBidFragment: AcceptBidFragment? = null

    @BindView(R.id.taskAuthorText)
    lateinit var taskAuthor: TextView
    @BindView(R.id.taskTitle)
    lateinit var taskTitle: TextView
    @BindView(R.id.taskDetails)
    lateinit var taskDetails: TextView
    @BindView(R.id.taskStatus)
    lateinit var taskStatus: TextView
    @BindView(R.id.taskPay)
    lateinit var taskPay: TextView
    @BindView(R.id.bidListView)
    lateinit var bidListView:RecyclerView
    @BindView(R.id.reopenButton)
    lateinit var reopenButton : Button
    @BindView(R.id.addBidOrMarkDone)
    lateinit var addOrMarkButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tasks)
        ButterKnife.bind(this)

        // TODO - Test getting Tasks via Intent
        if (intent != null) {
            val taskStr = intent.getStringExtra("TASK")
            if (taskStr != null) {
                displayTask = GenerateRetrofit.generateGson().fromJson(taskStr, Task::class.java)
                updateDetails()
                taskBidList = displayTask!!.bids
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
        acceptBidFragment!!.arguments = args
        acceptBidFragment!!.show(fragmentManager, "DialogFragment")
    }

    private fun getUserType() {
        var editor = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE)
        //username = editor.getString("Username", null)
        if (username == displayTask?.owner) {
            isRequester = true
        }
    }

    private fun updateDetails() {
        taskAuthor.text = displayTask?.owner
        taskTitle.text = displayTask?.title
        taskDetails.text = displayTask?.description
        taskStatus.text = displayTask?.status?.name
    }

    override fun bidUpdate(bidAmount : Double, originalBid : Bid) {
        if (displayTask != null) {
            var index: Int = displayTask!!.bids.indexOf(originalBid)
            var changedBid = Bid(displayTask!!.bids[index].owner, bidAmount)
            displayTask!!.bids[index] = changedBid
            updateDisplayTask()
        }

    }

    override fun bidAdd(bidAmount : Double) {
        var newBid = Bid(username, bidAmount)
        if (displayTask != null) {
            displayTask!!.bids.add(newBid)
            updateDisplayTask()
        }
    }

    override fun declinedBid(bid: Bid) {
        Log.i("Hit", "Decline")
    }

    override fun acceptedBid(bid: Bid) {
        Log.i("MESSAGE", "RE")
    }

    @OnClick(R.id.addBidOrMarkDone)
    fun addBid(view : View) {
        if (isRequester) {
            Log.i("G", "G")
        } else {
            editBidFragment = EditBidFragment()
            var args = Bundle()
            editBidFragment!!.arguments = args
            editBidFragment!!.show(fragmentManager, "DialogFragment")
        }
    }

    private fun updateDisplayTask() {
        GenerateRetrofit.generateRetrofit()
    }
}
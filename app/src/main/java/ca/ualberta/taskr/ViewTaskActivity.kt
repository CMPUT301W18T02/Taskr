package ca.ualberta.taskr

/**
 * Created by Jacob Bakker on 3/12/2018.
 */

import android.content.Context
import android.content.Intent
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.constraint.ConstraintSet
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewStub
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnItemClick
import ca.ualberta.taskr.R.attr.layoutManager
import ca.ualberta.taskr.adapters.BidListAdapter
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import kotlinx.android.synthetic.main.activity_view_tasks.*

class ViewTaskActivity: AppCompatActivity() {

    private var isRequester: Boolean = false
    private var taskOwner: String = ""
    private var taskBidList: ArrayList<Bid> = ArrayList()
    private var bidListAdapter: BidListAdapter = BidListAdapter(taskBidList)
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var displayTask: Task? = null

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
    @BindView(R.id.addBids)
    lateinit var addBidsButton : Button

    @OnItemClick(R.id.bidListView)
    fun onItemCLick(position: Int) {
        var index = bidListAdapter.getItemId(position) as Int
        val stringBid = GenerateRetrofit.generateGson().toJson(taskBidList[index], Bid::class.java)

        var intent = Intent(this, EditBidFragment::class.java)
        intent.putExtra("BID", stringBid)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tasks)
        ButterKnife.bind(this)
        if (isRequester) {
            addBidsButton.text = getResources().getString(R.string.activity_view_tasks_provider_done)
            reopenButton.visibility = View.VISIBLE
        }

        // TODO - Get Task via intent
        val intent = getIntent()
        if (intent != null) {
            val taskSerial = intent.getSerializableExtra("TASK")
            if (taskSerial != null) {
                displayTask = taskSerial as Task
                updateDetails()
            }
        }
        updateDetails()

        viewManager = LinearLayoutManager(this)
        bidListView.apply {
            layoutManager = viewManager
            adapter = bidListAdapter
        }

        taskBidList.add(Bid("Bob", 4.20))
        taskBidList.add(Bid("Bob", 4.20))
        taskBidList.add(Bid("Bob", 4.20))
        bidListAdapter.notifyDataSetChanged()
    }

    private fun getUserNameAndType() {
        var editor = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE)
        taskOwner = editor.getString("Username", null)
        if (taskOwner == displayTask?.owner) {
            isRequester = true
        }
    }

    private fun updateDetails() {
        taskAuthor.text = displayTask?.owner
        taskTitle.text = displayTask?.title
        taskDetails.text = displayTask?.description
        taskStatus.text = displayTask?.status?.name
    }
}
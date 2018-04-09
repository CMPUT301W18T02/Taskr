package ca.ualberta.taskr.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ca.ualberta.taskr.R
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.util.PhotoConversion
import java.text.DecimalFormat

/**
 * BidListAdapter Class. This class takes in an [ArrayList] of Bids and produces a [RecyclerView.Adapter]
 * for displaying the views of bids put out by a specific [User]
 *
 * @property masterTaskList An [ArrayList] containing all of the bids
 * @property username A [String] representing the user
 * @constructor initializes the bidlist and links into the [OnItemClickListener]
 * @see [RecyclerView.Adapter]
 */
class MyBidsListAdapter(masterTaskList: ArrayList<Task>, username: String) : RecyclerView.Adapter<MyBidsListAdapter.LocalViewHolder>() {
    private var taskList: List<Task> = masterTaskList
    private var username: String = username

    private lateinit var mClickListener: View.OnClickListener

    /**
     * Local view of the [MyBidsListAdapter]
     * @property view the specified [View] containing the local view
     * @constructor Set the [OnItemClickListener] value
     * @see [RecyclerView.ViewHolder]
     * @see [View.OnClickListener]
     */
    class LocalViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var taskHeaderImage: ImageView = view.findViewById(R.id.taskHeaderImage)
        var taskTitle: TextView = view.findViewById(R.id.taskTitle)
        var taskDesc: TextView = view.findViewById(R.id.taskDesc)
        var taskStatus: TextView = view.findViewById(R.id.taskStatus)
        var taskLowestBid: TextView = view.findViewById(R.id.taskLowestBid)
        var myBid: TextView = view.findViewById(R.id.myCurrentBid)

        override fun onClick(item: View?) {
            Log.e("TEST", "onClick $adapterPosition");
        }

    }

    /**
     * Create a view for a selected view
     * @param parent the [ViewGroup] the viewHolder is a part of
     * @param viewType the type of view
     * @return an instance of [LocalViewHolder] containing a view pointing towards our item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_my_bids, parent, false)

        val holder = LocalViewHolder(itemView)
        holder.itemView.setOnClickListener({ view -> mClickListener.onClick(view) })
        return holder
    }

    /**
     * Bind a selected view
     * @param holder the [LocalViewHolder] to bind
     * @param position the position within the view to bind
     */
    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskTitle.text = task.title
        holder.taskDesc.text = task.description
        holder.taskStatus.text = task.status.toString()
        val lowestBid = task.bids.minBy { it ->  it.amount }
        val myBids = task.bids.filter { it -> it.owner == username}
        val myLowestBid = myBids.minBy { it ->  it.amount }
        val moneyFormat = DecimalFormat("0.00")
        if (lowestBid != null){
            holder.taskLowestBid.text = "Top Bid: $" + moneyFormat.format(lowestBid.amount)
        }
        else{
            holder.taskLowestBid.text = "No bid!"
        }
        if (myLowestBid != null){
            holder.myBid.text = "My bid: $" + moneyFormat.format(myLowestBid.amount)
        }
        else{
            holder.myBid.text = "No bid!"
        }
        if (task.photos.size != 0 && task.photos[0] != null){
            holder.taskHeaderImage.visibility = View.VISIBLE
            holder.taskHeaderImage.setImageBitmap(PhotoConversion.getBitmapFromString(task.photos[0]))
        }
        else {
            holder.taskHeaderImage.visibility = View.GONE

        }
    }

    /**
     * Return the number of items in the task list
     * @return the size of the list
     */
    override fun getItemCount(): Int {
        return taskList.size
    }

    /**
     * Set the click listener to a callback function
     * @param callback a [View.OnClickListener]
     */
    fun setClickListener(callback: View.OnClickListener) {
        mClickListener = callback
    }
}
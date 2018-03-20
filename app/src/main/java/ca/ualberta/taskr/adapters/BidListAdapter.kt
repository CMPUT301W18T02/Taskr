package ca.ualberta.taskr.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ca.ualberta.taskr.R
import ca.ualberta.taskr.adapters.BidListAdapter.OnItemClickListener
import ca.ualberta.taskr.models.Bid

/**
 * Created by Jacob Bakker on 3/15/2018.
 */

/**
 * BidListAdapter Class. This class takes in an [ArrayList] of Bids and produces a [RecyclerView.Adapter]
 * for displaying the views.
 *
 * @property taskBidList An [ArrayList] containing all of the bids
 * @constructor initializes the bidlist and links into the [OnItemClickListener]
 * @see [RecyclerView.Adapter]
 */
class BidListAdapter(taskBidList: ArrayList<Bid>): RecyclerView.Adapter<BidListAdapter.LocalViewHolder>() {

    private var bidList: ArrayList<Bid> = taskBidList
    var itemClickListener : OnItemClickListener? = null

    /**
     * Local view of the [BidListAdapter]
     * @property view the specified [View] containing the local view
     * @constructor Set the [OnItemClickListener] value
     * @see [RecyclerView.ViewHolder]
     * @see [View.OnClickListener]
     */
    inner class LocalViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var bidderImage: ImageView = view.findViewById(R.id.bidderImage)
        var bidderName: TextView = view.findViewById(R.id.bidderName)
        var bidderAmount: TextView = view.findViewById(R.id.bidAmount)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            this@BidListAdapter.itemClickListener?.onItemClick(v, adapterPosition)
        }
    }

    /**
     * OnItemClickListener interface implementation
     */
    interface OnItemClickListener {

        /**
         * Called when an item is clicked
         * @property view The specified [View]
         * @property position The position within the ListView
         */
        fun onItemClick(view : View, position : Int)
    }

    /**
     * Sets the itemClickListener
     * @property itemClickListener the [OnItemClickListener] instance
     */
    fun setOnItemClickListener(itemClickListener : OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    /**
     * Create a view for a selected view
     * @property parent the [ViewGroup] the viewHolder is a part of
     * @property viewType the type of view
     * @return an instance of [LocalViewHolder] containing a view pointing towards our item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_bid, parent, false)
        return LocalViewHolder(itemView)
    }

    /**
     * Bind a selected view
     * @property holder the [LocalViewHolder] to bind
     * @property position the position within the view to bind
     */
    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val bid = bidList[position]
        holder.bidderName.text = bid.owner
        holder.bidderAmount.text = String.format(holder.bidderAmount.text.toString(), bid.amount)
    }

    /**
     * Return the number of items in the task list
     * @return the size of the list
     */
    override fun getItemCount(): Int {
        return bidList.size
    }

}
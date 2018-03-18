package ca.ualberta.taskr.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import ca.ualberta.taskr.R
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import kotlinx.android.synthetic.main.row_bid.view.*
import javax.security.auth.callback.Callback

/**
 * Created by Jacob Bakker on 3/15/2018.
 */

class BidListAdapter(taskBidList: ArrayList<Bid>): RecyclerView.Adapter<BidListAdapter.LocalViewHolder>() {

    private var bidList: ArrayList<Bid> = taskBidList

    class LocalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var bidderImage: ImageView = view.findViewById(R.id.bidderImage)
        var bidderName: TextView = view.findViewById(R.id.bidderName)
        var bidderAmount: TextView = view.findViewById(R.id.bidAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_bid, parent, false)
        return LocalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val bid = bidList[position]
        holder.bidderName.text = bid.owner
        holder.bidderAmount.text = bid.amount.toString()
    }

    override fun getItemCount(): Int {
        return bidList.size
    }

}
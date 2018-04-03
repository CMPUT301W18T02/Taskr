package ca.ualberta.taskr.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import ca.ualberta.taskr.R
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import kotlinx.android.synthetic.main.row_bid.view.*
import org.w3c.dom.Text
import javax.security.auth.callback.Callback

/**
 * Created by Jacob Bakker on 3/15/2018.
 */

class BidListAdapter(taskBidList: ArrayList<Bid>): RecyclerView.Adapter<BidListAdapter.LocalViewHolder>() {

    @BindView(R.id.bidderName)
    private lateinit var bidderNameView : TextView
    private var bidList: ArrayList<Bid> = taskBidList
    var itemClickListener : OnItemClickListener? = null

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

    interface OnItemClickListener {
        fun onItemClick(view : View, position : Int)
    }

    fun setOnItemClickListener(itemClickListener : OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_bid, parent, false)
        return LocalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val bid = bidList[position]
        holder.bidderName.text = bid.owner
        holder.bidderAmount.text = String.format(holder.bidderAmount.text.toString(), bid.amount)
    }
    override fun getItemCount(): Int {
        return bidList.size
    }

    @OnClick(R.id.bidderName)
    fun openUserProfile() {

    }
}
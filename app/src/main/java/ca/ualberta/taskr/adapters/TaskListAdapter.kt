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


/**
 * TaskListAdapter class. Take in an [ArrayList] of Tasks and produce an [RecyclerView.Adapter]
 * for displaying the views
 * @property masterTaskList An [ArrayList] of all the passed in tasks
 * @see [RecyclerView.Adapter]
 */
class TaskListAdapter(masterTaskList: ArrayList<Task>) : RecyclerView.Adapter<TaskListAdapter.LocalViewHolder>() {

    private var taskList: List<Task> = masterTaskList

    private lateinit var mClickListener: View.OnClickListener

    /**
     * Local view of the [TaskListAdapter]
     * @property view the specified [View] containing the local view
     * @constructor copy the [ArrayList] of Tasks into the class
     * @see [RecyclerView.ViewHolder]
     * @see [View.OnClickListener]
     */
    class LocalViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var taskHeaderImage: ImageView = view.findViewById(R.id.taskHeaderImage)
        var taskTitle: TextView = view.findViewById(R.id.taskTitle)
        var taskDesc: TextView = view.findViewById(R.id.taskDesc)
        var taskStatus: TextView = view.findViewById(R.id.taskStatus)
        var taskLowestBid: TextView = view.findViewById(R.id.taskLowestBid)

        override fun onClick(item: View?) {
            Log.e("TEST", "onClick " + getAdapterPosition());
        }

    }

    /**
     * Create a view for a selected view
     * @property parent the [ViewGroup] the viewHolder is a part of
     * @property viewType the type of view
     * @return an instance of [LocalViewHolder] containing a view pointing towards our item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_task_item, parent, false)

        //return LocalViewHolder(itemView)

        val holder = LocalViewHolder(itemView)
        holder.itemView.setOnClickListener({ view -> mClickListener.onClick(view) })
        return holder
    }

    /**
     * Bind a selected view
     * @property holder the [LocalViewHolder] to bind
     * @property position the position within the view to bind
     */
    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskTitle.text = task.title
        holder.taskDesc.text = task.description
        holder.taskStatus.text = task.status.toString()
        val lowestBid = task.bids.minBy { it ->  it.amount }
        if (lowestBid != null){
            holder.taskLowestBid.text = "Top Bid: $" + lowestBid.amount
        }
        else{
            holder.taskLowestBid.text = "No bid!"
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
     * Set the callback click function
     * @property callback take in a view's [View.OnClickListener] and produce a callback
     */
    fun setClickListener(callback: View.OnClickListener) {
        mClickListener = callback
    }
}
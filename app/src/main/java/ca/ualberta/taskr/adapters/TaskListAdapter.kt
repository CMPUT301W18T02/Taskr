package ca.ualberta.taskr.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ca.ualberta.taskr.R
import ca.ualberta.taskr.models.Task

/**
 * TaskListAdapter class. Take in a task list and produce an adapter subclass that allows
 * lists of tasks to be used with the recyclerview view
 */
class TaskListAdapter(masterTaskList: ArrayList<Task>) : RecyclerView.Adapter<TaskListAdapter.LocalViewHolder>() {
    private var taskList: List<Task> = masterTaskList

    /**
     * LocalViewHolder function
     */
    class LocalViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var taskHeaderImage: ImageView = view.findViewById(R.id.taskHeaderImage)
        var taskTitle: TextView = view.findViewById(R.id.taskTitle)
        var taskDesc: TextView = view.findViewById(R.id.taskDesc)
        var taskStatus: TextView = view.findViewById(R.id.taskStatus)
        var taskLowestBid: TextView = view.findViewById(R.id.taskLowestBid)

    }

    /**
     * Create the LocalViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_task_item, parent, false)

        return LocalViewHolder(itemView)
    }

    /**
     * Bind a subset window of the dataset to the LocalViewHolder
     */
    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskTitle.text = task.title
        holder.taskDesc.text = task.description
        holder.taskStatus.text = task.status.toString()
        val lowestBid = task.bids.minBy { it ->  it.amount }
        if (lowestBid != null){
            holder.taskLowestBid.text = lowestBid.amount.toString()
        }
        else{
            holder.taskLowestBid.text = "No bid!"
        }
    }
    /**
     * Return the size of the dataset
     */
    override fun getItemCount(): Int {
        return taskList.size
    }
}
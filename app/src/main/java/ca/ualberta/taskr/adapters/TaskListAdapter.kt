package ca.ualberta.taskr.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ca.ualberta.taskr.R
import ca.ualberta.taskr.models.Task
import org.androidannotations.annotations.ViewById


class TaskListAdapter(masterTaskList: ArrayList<Task>) : RecyclerView.Adapter<TaskListAdapter.LocalViewHolder>() {
    private var taskList: List<Task> = masterTaskList

    inner class LocalViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @ViewById
        lateinit var taskHeaderImage: ImageView

        @ViewById
        lateinit var taskTitle: TextView

        @ViewById
        lateinit var taskDesc: TextView

        @ViewById
        lateinit var taskStatus: TextView

        @ViewById
        lateinit var taskLowestBid: TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_task_item, parent, false)

        return LocalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskTitle.text = task.title
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}
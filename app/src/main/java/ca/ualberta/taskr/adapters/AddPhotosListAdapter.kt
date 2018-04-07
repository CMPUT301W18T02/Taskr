package ca.ualberta.taskr.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ca.ualberta.taskr.R
import ca.ualberta.taskr.util.PhotoConversion


/**
 * AddPhotosListAdapter class.
 */
class AddPhotosListAdapter(photosList: ArrayList<String>) : RecyclerView.Adapter<AddPhotosListAdapter.LocalViewHolder>() {
    private var photosList: List<String> = photosList

    private lateinit var mClickListener: View.OnClickListener

    /**
     * LocalViewHolder function
     */
    class LocalViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var photoToBeAdded: ImageView = view.findViewById(R.id.toBeAddedPhoto)

        override fun onClick(item: View?) {

        }
    }

    /**
     * Create the LocalViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_add_photo, parent, false)

        val holder = LocalViewHolder(itemView)
        holder.itemView.setOnClickListener({ view -> mClickListener.onClick(view) })
        return holder
    }

    /**
     * Bind a subset window of the dataset to the LocalViewHolder
     */
    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val photo = photosList[position]
        holder.photoToBeAdded.setImageBitmap(PhotoConversion.getBitmapFromString(photo))
    }
    /**
     * Return the size of the dataset
     */
    override fun getItemCount(): Int {
        return photosList.size
    }

    fun setClickListener(callback: View.OnClickListener) {
        mClickListener = callback
    }
}
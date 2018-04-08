package ca.ualberta.taskr.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ca.ualberta.taskr.R
import ca.ualberta.taskr.util.PhotoConversion


/**
 * AddPhotosListAdapter class. This class takes in an [ArrayList] of encoded photo strings and
 * produces a [RecyclerView.Adapter] for displaying the photos in that [ArrayList].
 *
 * @param photosList List of photos to show
 * @see RecyclerView.Adapter
 */
class PhotosListAdapter(photosList: ArrayList<String>) : RecyclerView.Adapter<PhotosListAdapter.LocalViewHolder>() {
    private var photosList: List<String> = photosList

    private lateinit var mClickListener: View.OnClickListener

    /**
     * Local view of the [PhotosListAdapter]
     * @param view the specified [View] containing the local view
     * @see [RecyclerView.ViewHolder]
     * @see [View.OnClickListener]
     */
    class LocalViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var photoToBeAdded: ImageView = view.findViewById(R.id.toBeAddedPhoto)

        override fun onClick(item: View?) {

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
                .inflate(R.layout.row_add_photo, parent, false)

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
        val photo = photosList[position]
        holder.photoToBeAdded.setImageBitmap(PhotoConversion.getBitmapFromString(photo))
    }
    /**
     * Get the size of the photos list
     * @return the size of the photos list
     */
    override fun getItemCount(): Int {
        return photosList.size
    }

    /**
     * Set the click listener to a callback function
     * @param callback a [View.OnClickListener]
     */
    fun setClickListener(callback: View.OnClickListener) {
        mClickListener = callback
    }
}
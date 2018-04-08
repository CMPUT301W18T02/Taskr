package ca.ualberta.taskr.models

import com.mapbox.mapboxsdk.geometry.LatLng
import java.io.Serializable

/**
 * Task Class. This class contained all the information specifying what a [Task] is
 * @property owner the owner
 * @property title the title
 * @property status the status of the task in the form of a [TaskStatus] enumeration
 * @property location the location of the form of a [LatLng] GPS Coordanate
 * @property photos a [List] of base 64 encoded [Image] objects stored as [String] objects
 */
data class Task(val owner: String, val title: String, var status: TaskStatus?,
                val bids: ArrayList<Bid>, val description: String, val photos: ArrayList<String>,
                val location: LatLng?, var chosenBidder: String ): Serializable {

    /**
     * Return a [Bid] at a specified index
     * @param index the index
     * @return the [Bid]
     */
    fun getBidAtIndex(index: Int): Bid {
        return this.bids[index]
    }

    /**
     * add a [Bid] to the task
     * @param newBid the bid to add [Bid]
     */
    fun addBid(newBid: Bid) {
        this.bids.add(newBid)
    }

    /**
     * update a [Bid] at a specified index
     * @param newBid the [Bid] that will be replacing the current [Bid] at the index
     * @param index the index to perform the replacment at
     */
    fun setBidAtIndex(newBid: Bid, index: Int) {
        this.bids[index] = newBid
    }

    /**
     * Return a photo from a specified index
     * @param index the photo to return
     * @return the base 64 encoded photo string
     */
    fun getPhotoAtIndex(index: Int): String {
        return this.photos[index]
    }

    /**
     * update the photo at a specified index
     * @param newPhoto the new photo
     * @param index the index
     */
    fun setPhotoAtIndex(newPhoto: String, index: Int) {
        this.photos[index] = newPhoto
    }

    /**
     * add a photo to the task
     * @param newPhoto the photo to add
     */
    fun addPhoto(newPhoto: String) {
        this.photos.add(newPhoto)
    }

}
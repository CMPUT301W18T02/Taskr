package ca.ualberta.taskr.models

import com.mapbox.mapboxsdk.geometry.LatLng
import java.io.Serializable

/**
 * Task Class
 */
data class Task(val owner: String, val title: String, var status: TaskStatus?,
                val bids: ArrayList<Bid>, val description: String, val photos: ArrayList<String>,
                val location: LatLng?, var chosenBidder: String ): Serializable {

    fun getBidAtIndex(index: Int): Bid {
        return this.bids[index]
    }

    fun addBid(newBid: Bid) {
        this.bids.add(newBid)
    }

    fun setBidAtIndex(newBid: Bid, index: Int) {
        this.bids[index] = newBid
    }

    fun getPhotoAtIndex(index: Int): String {
        return this.photos[index]
    }

    fun setPhotoAtIndex(newPhoto: String, index: Int) {
        this.photos[index] = newPhoto
    }

    fun addPhoto(newPhoto: String) {
        this.photos.add(newPhoto)
    }

}
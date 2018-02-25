package ca.ualberta.taskr.models

import android.media.Image
import com.mapbox.mapboxsdk.geometry.LatLng


data class Task(val owner: String, val title: String, var status: TaskStatus?,
                val bids: ArrayList<Bid>, val description: String, val photos: ArrayList<Image>,
                val location: LatLng?, val chosenBidder: String ) {

    fun getBidAtIndex(index: Int): Bid {
        return this.bids[index]
    }

    fun addBid(newBid: Bid) {
        this.bids.add(newBid)
    }

    fun setBidAtIndex(newBid: Bid, index: Int) {
        this.bids[index] = newBid;
    }

    fun getPhotoAtIndex(index: Int): Image {
        return this.photos[index]
    }

    fun setPhotoAtIndex(newPhoto: Image, index: Int) {
        this.photos[index] = newPhoto
    }

    fun addPhoto(newPhoto: Image) {
        this.photos.add(newPhoto)
    }
}
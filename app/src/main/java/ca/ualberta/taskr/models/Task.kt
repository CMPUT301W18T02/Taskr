package ca.ualberta.taskr.models

import android.media.Image
import com.mapbox.mapboxsdk.geometry.LatLng


data class Task(val owner: String, val title: String, var status: TaskStatus,
                val bids: ArrayList<Bid>, val description: String, val photos: ArrayList<Image>,
                val location: LatLng, val chosenBidder: String )
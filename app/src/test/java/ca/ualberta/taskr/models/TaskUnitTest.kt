package ca.ualberta.taskr.models

/**
 * Created by Jacob Bakker on 2/22/2018.
 */

import android.media.Image

import com.mapbox.mapboxsdk.geometry.LatLng

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

import java.util.ArrayList

import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.TaskStatus

import org.junit.Assert.assertEquals

@RunWith(RobolectricTestRunner::class)
class TaskUnitTest {
    private var owner = "Corkus"
    private var title = "I'm a wot"
    private var status: TaskStatus? = null
    private var bids = ArrayList<Bid>()
    private var description = "4 mana 7/7"
    private var photos = ArrayList<String>()
    private var location: LatLng? = null
    private var chosenBidder = "The Mask"

    private var newBidName1 = "Mr. MoneyBags McGee's Monetary Mmmm"
    private var newBidName2 = "A Bribe"
    private var newBidAmount1 = 0.01f
    private var newBidAmount2 = 6.66f


    @Test
    fun testGetOwner() {
        val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)
        assertEquals(owner, task.owner)
    }

    @Test
    fun testGetTitle() {
        val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)
        assertEquals(title, task.title)
    }

    @Test
    fun testGetStatus() {
        val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)
        assertEquals(status, task.status)
    }

    @Test
    fun testGetBids() {
        val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)
        assertEquals(bids, task.bids)
    }

    @Test
    fun testAddAndGetBid() {
        val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)
        val newBid1 = Bid(newBidName1, newBidAmount1.toDouble(), false)
        val newBid2 = Bid(newBidName2, newBidAmount2.toDouble(), false)
        task.addBid(newBid1)
        task.addBid(newBid2)
        var testBid = task.getBidAtIndex(0)
        assertEquals(newBid1, testBid)
        testBid = task.getBidAtIndex(1)
        assertEquals(newBid2, testBid)
    }

    @Test
    fun testSetBidAtIndex() {
        val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)
        val newBid1 = Bid(newBidName1, newBidAmount1.toDouble(), false)
        val newBid2 = Bid(newBidName2, newBidAmount2.toDouble(), false)
        task.addBid(newBid1)
        task.setBidAtIndex(newBid2, 0)
        val testBid = task.getBidAtIndex(0)
        assertEquals(newBid2, testBid)
    }

    @Test
    fun testGetDescription() {
        val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)
        assertEquals(description, task.description)
    }

    @Test
    fun testGetPhotos() {
        val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)
        assertEquals(photos, task.photos)
    }

    @Test
    fun testGetLocation() {
        val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)
        assertEquals(location, task.location)
    }

    @Test
    fun testGetChosenBidder() {
        val task = Task(owner, title, status, bids, description, photos, location, chosenBidder)
        assertEquals(chosenBidder, task.chosenBidder)
    }

    @Test
    fun testCloseTask(){}

    @Test
    fun testReopenTask(){}

    @Test
    fun testIsTaskOpenForBidding(){}

}

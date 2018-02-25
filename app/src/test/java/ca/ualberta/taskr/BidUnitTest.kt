package ca.ualberta.taskr

import ca.ualberta.taskr.models.Bid
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Created by james on 25/02/18.
 */
@RunWith(RobolectricTestRunner::class)
class BidUnitTest {

    private val bidVal = 10.00
    private val bidder = "Richard B. Small"

    @Test
    fun testGetBidder(){
        val bid = Bid(bidder, bidVal)
        val otherBidder = bid.bidder
        assertEquals(bidder, otherBidder)
    }

    @Test
    fun testGetValue(){
        val bid = Bid(bidder, bidVal)
        val otherBidVal = bid.value
        assertEquals(bidVal, otherBidVal, 0.001)
    }

}

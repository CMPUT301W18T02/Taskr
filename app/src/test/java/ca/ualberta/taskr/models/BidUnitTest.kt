package ca.ualberta.taskr.models

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

    private val amount = 10.00
    private val owner = "Richard B. Small"

    @Test
    fun testGetBidder(){
        val bid = Bid(owner, amount)
        val otherBidder = bid.owner
        assertEquals(owner, otherBidder)
    }

    @Test
    fun testGetValue(){
        val bid = Bid(owner, amount)
        val otherBidVal = bid.amount
        assertEquals(amount, otherBidVal, 0.001)
    }

}

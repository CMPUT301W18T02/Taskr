package ca.ualberta.taskr.models

import ca.ualberta.taskr.BuildConfig
import ca.ualberta.taskr.models.Bid
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by james on 25/02/18.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(26))       // this magical line fixes every single travis test fail
class BidUnitTest {

    private val amount = 10.00
    private val owner = "Richard B. Small"

    @Test
    fun testGetBidder(){
        val bid = Bid(owner, amount, false)
        val otherBidder = bid.owner
        assertEquals(owner, otherBidder)
    }

    @Test
    fun testGetValue(){
        val bid = Bid(owner, amount, false)
        val otherBidVal = bid.amount
        assertEquals(amount, otherBidVal, 0.001)
    }

}

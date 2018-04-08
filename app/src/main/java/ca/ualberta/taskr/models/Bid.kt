package ca.ualberta.taskr.models

/**
 * A bid for a task
 *
 * Data class for representing bids
 *
 * @property owner user that bidded on the task
 * @property amount the value of the bid in dollars
 * @constructor creates a new bid
 */
data class Bid(val owner: String, val amount: Double, val isDismissed: Boolean)
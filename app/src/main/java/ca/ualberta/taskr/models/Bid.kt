package ca.ualberta.taskr.models

/**
 * Bid class. Contains the name of the individual that produced the bid as well as the amount
 * that they bid
 */
data class Bid(val owner: String, val amount: Double)
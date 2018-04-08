package ca.ualberta.taskr.models

/**
 * TaskStatus Enumeration. Shows the various states that a task might be in
 * -REQUESTED: The task has been requested, but nobody has bid on it yet
 * -BID: The task has been bid on
 * -ASSIGNED: The task has been assigned to a bidder
 * -DONE: The task has been completed by a bidder
 */
enum class TaskStatus {
    REQUESTED, BID, ASSIGNED, DONE
}
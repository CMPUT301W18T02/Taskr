package ca.ualberta.taskr.exceptions

/**
 * UserDoesNotExistException. This is thrown when a [User] does not exist
 * @exception UserDoesNotExistException
 */
class UserDoesNotExistException(message: String) : Exception("User: $message does not exist")
package ca.ualberta.taskr.exceptions

/**
 * UserAlreadyExistsException. This is thrown when a [User] already exists
 * @exception UserAlreadyExistsException
 */
class UserAlreadyExistsException(message: String) : Exception("User: $message already exists")
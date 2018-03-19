package ca.ualberta.taskr.exceptions


class UserDoesNotExistException(message: String) : Exception("User: $message does not exist")
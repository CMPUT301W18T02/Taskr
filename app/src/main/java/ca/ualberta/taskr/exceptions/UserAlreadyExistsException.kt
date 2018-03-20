package ca.ualberta.taskr.exceptions


class UserAlreadyExistsException(message: String) : Exception("User: $message already exists")
package ca.ualberta.taskr.models

/**
 * User Class. This class contains all the Specifications for the [User]
 * @property name the name
 * @property phoneNumber the user's phone number
 * @property profilePicture the user's profile picture
 * @property email the user's email address
 * @property username the user's username
 */
data class User(val name: String, val phoneNumber: String, val profilePicture: String?,
                val email: String, val username: String)

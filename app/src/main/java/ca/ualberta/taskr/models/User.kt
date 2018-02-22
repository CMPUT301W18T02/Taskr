package ca.ualberta.taskr.models

import android.media.Image


data class User(var name: String, var phoneNumber: String, var profilePicture: Image?,
                var email: String, val username: String)

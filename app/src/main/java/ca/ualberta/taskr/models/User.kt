package ca.ualberta.taskr.models

import android.media.Image


data class User(val name: String, val phoneNumber: String, val profilePicture: Image,
                val Email: String, val username: String)
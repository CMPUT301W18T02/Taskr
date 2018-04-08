package ca.ualberta.taskr.models

import android.media.Image

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

import ca.ualberta.taskr.models.User

import org.junit.Assert.assertEquals

/**
 * Created by marissasnihur on 2018-02-22.
 *
 */
@RunWith(RobolectricTestRunner::class)
class UserUnitTest {

    private var name = "John"
    private var email = "jsmith@ualberta.ca"
    private var phoneNumber = "1234567890"
    private var username = "jsmith"
    private var image: String? = null


    /**
     * test getting a users name
     */
    @Test
    fun testGetName() {
        val usr = User(name, phoneNumber, image, email, username)
        assertEquals(name, usr.name)
    }

    /**
     * test getting a users email
     */
    @Test
    fun testGetEmail() {
        val usr = User(name, phoneNumber, image, email, username)
        assertEquals(email, usr.email)
    }

    /**
     * test getting a users phone number
     */
    @Test
    fun testGetPhoneNumber() {
        val usr = User(name, phoneNumber, image, email, username)
        assertEquals(phoneNumber, usr.phoneNumber)
    }

    /**
     * test getting a users username
     */
    @Test
    fun testGetUsername() {
        val usr = User(name, phoneNumber, image, email, username)
        assertEquals(username, usr.username)
    }

    /**
     * test getting a users image
     */
    @Test
    fun testGetImage() {
        val usr = User(name, phoneNumber, image, email, username)
        assertEquals(image, usr.profilePicture)
    }
}

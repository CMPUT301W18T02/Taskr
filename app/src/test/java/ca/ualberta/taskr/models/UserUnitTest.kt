package ca.ualberta.taskr.models

import android.media.Image

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

import ca.ualberta.taskr.models.User

import org.junit.Assert.assertEquals

/**
 * Created by marissasnihur on 2018-02-22.
 */
@RunWith(RobolectricTestRunner::class)
class UserUnitTest {

    private var name = "John"
    private var email = "jsmith@ualberta.ca"
    private var phoneNumber = "1234567890"
    private var username = "jsmith"
    private var image: Image? = null


    @Test
    fun testGetName() {
        val usr = User(name, phoneNumber, image, email, username)
        assertEquals(name, usr.name)
    }

    @Test
    fun testGetEmail() {
        val usr = User(name, phoneNumber, image, email, username)
        assertEquals(email, usr.email)
    }

    @Test
    fun testGetPhoneNumber() {
        val usr = User(name, phoneNumber, image, email, username)
        assertEquals(phoneNumber, usr.phoneNumber)
    }

    @Test
    fun testGetUsername() {
        val usr = User(name, phoneNumber, image, email, username)
        assertEquals(username, usr.username)
    }

    @Test
    fun testGetImage() {
        val usr = User(name, phoneNumber, image, email, username)
        assertEquals(image, usr.profilePicture)
    }
}

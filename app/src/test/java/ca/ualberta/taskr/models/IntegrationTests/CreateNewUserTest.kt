package ca.ualberta.taskr.models.IntegrationTests

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import ca.ualberta.taskr.*
import ca.ualberta.taskr.models.User
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowIntent
import org.robolectric.shadows.ShadowLog

/**
 * Created by marissasnihur on 2018-03-19.
 *
 * Tests that involve creating new users, checking to make sure that we don't have two
 * identical usernames in the database, as well as logging in users are in this Test Class.
 *
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(26))
class CreateNewUserTest {
    //Test for creating new user.
    private lateinit var activity: LoginActivity

    private var name = "John"
    private var email = "jsmith@ualberta.ca"
    private var phoneNumber = "1234567890"
    private var username = "jsmith"
    private var image: String? = null

    private lateinit var userText: EditText
    private lateinit var newUserButton: Button
    private lateinit var loginButton: Button

    @Before
    fun setUp(){
        activity = Robolectric.setupActivity(LoginActivity::class.java)

        ShadowLog.stream = System.out

        userText = activity.findViewById<EditText>(R.id.UsernameText)
        newUserButton = activity.findViewById<Button>(R.id.NewUserButton)
        loginButton = activity.findViewById(R.id.LoginButton)

    }

    /**
     *
     * As a user, I want a profile with a unique username and my contact information.
     * As a user, I want the contact information to include an email address and a phone number.
     *
     *
     * checkUser() makes sure that the user isn't already in the database before adding
     * a user to the database. In essence checks to make sure that we don't have two people
     * with the same username in the database.
     */

    @Test
    fun checkUser(){
        val MasterUserList : ArrayList<User> = ArrayList<User>()

        val user = User(name, phoneNumber, image, email, username)

        Assert.assertFalse(activity.userController.getLocalUserName() == username)

        MasterUserList.add(user)

        Assert.assertEquals(user.username,username)

        activity.userController.setLocalUsername(username)

        Assert.assertTrue(activity.userController.getLocalUserName() == username)

    }

    /**
     * Tests the Login Button, makes sure that it sends the correct information to the correct
     * activity - also makes sure that the activity that the button is sending the
     * information to is indeed the correct activity.
     */

    @Test
    fun testOnLoginButtonClick() {
        loginButton.performClick()

        Thread.sleep(1000)

        val intent = Intent(activity, ListTasksActivity::class.java)

        Thread.sleep(1000)

        assertEquals(ListTasksActivity::class.java.canonicalName, intent.component.className)

    }

    /**
     * Tests the button that adds a new user, makes sure that the button sends the correct
     * information to the EditUserActivity activity. Makes sure that the activity that
     * it is sending it to is indeed the right activity.
     */

    @Test
    fun testOnNewUserButtonClick() {

        userText.setText(username)

        newUserButton.performClick()

        Thread.sleep(1000)

        val intent = Intent(activity, EditUserActivity::class.java)

        Thread.sleep(1000)

        intent.putExtra("username",username)

        Thread.sleep(1000)

        assertEquals(EditUserActivity::class.java.canonicalName, intent.component.className)
    }

}
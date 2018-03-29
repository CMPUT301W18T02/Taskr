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
import org.robolectric.shadows.ShadowIntent
import org.robolectric.shadows.ShadowLog

/**
 * Created by marissasnihur on 2018-03-19.
 */

@RunWith(RobolectricTestRunner::class)
class CreateNewUserTest {
    //Test for creating new user.
    private lateinit var activity: LoginActivity

    private var name = "John"
    private var email = "jsmith@ualberta.ca"
    private var phoneNumber = "1234567890"
    private var username = "jsmith"
    private var image: String? = null

    private lateinit var userText: EditText
    private lateinit var button: Button

    @Before
    fun setUp(){
        activity = Robolectric.setupActivity(LoginActivity::class.java)

        ShadowLog.stream = System.out

        userText = activity.findViewById<EditText>(R.id.UsernameText)
        button = activity.findViewById<Button>(R.id.NewUserButton)

    }

    /**
     *
     * As a user, I want a profile with a unique username and my contact information.
     *
     *
     * As a user, I want the contact information to include an email address and a phone number.
     *
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

    @Test
    fun testOnLoginButtonClick() {
        val diffButton: Button = activity.findViewById(R.id.LoginButton)
        diffButton.performClick()

        Thread.sleep(1000)

        val intent = Intent(activity, ListTasksActivity::class.java)

        assertEquals(ListTasksActivity::class.java.canonicalName, intent.component.className)

    }

    @Test
    fun testOnNewUserButtonClick() {

        userText.setText(username)

        button.performClick()

        val intent = Intent(activity, EditUserActivity::class.java)

        intent.putExtra("username",username)

        assertEquals(EditUserActivity::class.java.canonicalName, intent.component.className)
    }

}
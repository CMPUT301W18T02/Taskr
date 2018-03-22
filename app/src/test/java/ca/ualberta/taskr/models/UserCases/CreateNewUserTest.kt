package ca.ualberta.taskr.models.UserCases

import android.content.Intent
import android.widget.Button
import ca.ualberta.taskr.*
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.User
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

/**
 * Created by marissasnihur on 2018-03-19.
 *
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

    @Before
    fun setUp(){
        activity = Robolectric.setupActivity(LoginActivity::class.java)

    }

    @Test
    fun checkUser(){
        val MasterUserList : ArrayList<User> = ArrayList<User>()

        val user = User(name, phoneNumber, image, email, username)

        Assert.assertFalse(activity.userController.getLocalUserName() == username)

        MasterUserList.add(user)

        Assert.assertEquals(user.username,username)

        activity.userController.setLocalUsername(username)

        Assert.assertTrue(activity.userController.getLocalUserName() == username)

        //Assert.assertTrue(activity.CheckIfUsernameExists(username))
    }

    @Test
    fun testOnLoginButtonClick() {
        val button: Button = activity.findViewById(R.id.LoginButton)
        button.performClick()

        val intent: Intent = Shadows.shadowOf(activity).peekNextStartedActivity()

        assertEquals(ListTasksActivity::class.java.canonicalName, intent.component.className)

    }

    @Test
    fun testOnNewUserButtonClick(){
        val button: Button = activity.findViewById(R.id.NewUserButton)
        button.performClick()

        val intent: Intent = Shadows.shadowOf(activity).peekNextStartedActivity()

        assertEquals(EditUserActivity::class.java.canonicalName,intent.component.className)
    }

}
package ca.ualberta.taskr.models.IntegrationTests

import android.content.Intent
import android.test.ActivityInstrumentationTestCase2
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import ca.ualberta.taskr.BuildConfig
import ca.ualberta.taskr.EditUserActivity
import ca.ualberta.taskr.LoginActivity
import ca.ualberta.taskr.R
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.AccessController.getContext

/**
 * Created by marissasnihur on 2018-03-28.
 *
 */


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(26))
class EditUserTest {

    lateinit var CurrentUser: User
    lateinit var OldUser: User

    lateinit var UserSurnameText: EditText
    lateinit var UserPhoneNumberText: EditText
    lateinit var UserEmailText: EditText
    lateinit var ApplyChangesButton: Button
    lateinit var EditUSerErrorTextView: TextView

    private var name = "bleh"
    private var phone = "1029381202"
    private var email = "bleh@skdfjh.com"

    private var nameChange = "Blah"
    private var phoneChange = "18009999999"
    private var emailChange = "blah@lalala.com"
    private lateinit var intent: Intent

    //var userController : UserController = UserController(activity)

    lateinit var activity: EditUserActivity
    private var username = "iamdumb69"

    @Before
    fun setUp(){

        intent = Intent(RuntimeEnvironment.application,EditUserActivity::class.java)
        intent.putExtra("username", username)

        activity = Robolectric.buildActivity(EditUserActivity::class.java, intent).create().get()

        //val userController = UserController(activity)
        //username = userController.getLocalUserName()

        UserSurnameText = activity.findViewById<EditText>(R.id.UserSurnameText)
        UserPhoneNumberText = activity.findViewById<EditText>(R.id.UserPhoneNumberText)
        UserEmailText = activity.findViewById<EditText>(R.id.UserEmailText)
        ApplyChangesButton = activity.findViewById<Button>(R.id.ApplyChangesButton)
        EditUSerErrorTextView = activity.findViewById<TextView>(R.id.EditUserErrorTextView)
    }

    private fun deleteTestUser(){
        //delete test user in elastic search, @JamesCook
        GenerateRetrofit.generateRetrofit().getUserID(Query.userQuery(username))
                .enqueue(object : Callback<ElasticsearchID> {
                    override fun onResponse(call: Call<ElasticsearchID>, response: Response<ElasticsearchID>) {
                        Log.i("network", response.body().toString())
                        val id = response.body() as ElasticsearchID
                        GenerateRetrofit.generateRetrofit().deleteTask(id.toString())
                    }

                    override fun onFailure(call: Call<ElasticsearchID>, t: Throwable) {
                        Log.e("network", "Network Failed!")
                        t.printStackTrace()
                        return
                    }
                })
    }

    /**
     * As a user, I want to edit the contact information in my profile.
     *
     */

    @Test
    fun checkApplyChangesButton() {

        OldUser = User(name, phone, null, email, username)

        UserSurnameText.setText(nameChange)
        UserPhoneNumberText.setText(phoneChange)
        UserEmailText.setText(emailChange)

        CurrentUser = User(nameChange, phoneChange, null, emailChange, username)

        ApplyChangesButton.performClick()

        Assert.assertFalse(CurrentUser.name == OldUser.name)
        Assert.assertFalse(CurrentUser.email == OldUser.name)
        Assert.assertFalse(CurrentUser.phoneNumber == OldUser.phoneNumber)

        activity.UpdateUser(CurrentUser)

        Assert.assertTrue(CurrentUser.name == nameChange)
        Assert.assertTrue(CurrentUser.phoneNumber == phoneChange)
        Assert.assertTrue(CurrentUser.email == emailChange)

        deleteTestUser()
    }

    //TODO: Add test
    @Test
    fun checkMaxUserLength(){
        Assert.assertTrue(true)
    }

}
package ca.ualberta.taskr

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.util.Alarm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * LoginActivity
 *
 * This class allows the user to login or create a new user, given a specified username.
 *
 * @author MichaelSteer
 * @author eyesniper2
 */
class LoginActivity : AppCompatActivity() {

    @BindView(R.id.LoginButton)
    lateinit var LoginButton: Button

    @BindView(R.id.NewUserButton)
    lateinit var NewUserButton: Button

    @BindView(R.id.LoginErrorText)
    lateinit var LoginErrorText: TextView

    @BindView(R.id.UsernameText)
    lateinit var UsernameText: EditText

    var masterUserList: ArrayList<User> = ArrayList()
    var userController: UserController = UserController(this)

    /**
     * Initializes the login activity.
     * Immediately launches app home page if user is still signed in from a previous
     * session.
     *
     * @param: savedInstanceState
     * @see [ButterKnife]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkIfUserIsLoggedIn()

        ButterKnife.bind(this)
        Alarm().setAlarm(this)
    }

    /**
     * This method is executed on activity startup. If the user is still logged
     * in from a previous session, the [ListTasksActivity] is started.
     *
     * @see [UserController]
     */
    private fun checkIfUserIsLoggedIn() {
        if (userController.getLocalUserName() != "") {
            launchTaskList()
        }
    }

    //TODO: Implement method for checking connection to ElasticSearch index.
    fun getConnectivityStatus() {

    }

    /**
     * Displays login error message to user.
     *
     * @param message
     */
    fun showLoginError(message: String) {
        Log.d("Username", message)
        LoginErrorText.text = message
    }

    /**
     * Attempts to login user using inputted username.
     * If username exists in User index, the username is stored to Shared Preferences to be later
     * used in other activities before the [ListTasksActivity] is launched. If the username does not
     * exist, an error is instead displayed.
     *
     * @param view
     * @see GenerateRetrofit
     * @see UserController
     */
    @OnClick(R.id.LoginButton)
    fun loginClicked(v: View) {

        val username: String = UsernameText.text.toString()

        GenerateRetrofit.generateRetrofit().getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                Log.i("network", response.body().toString())
                masterUserList.addAll(response.body() as ArrayList<User>)
                val matchedUser = masterUserList.firstOrNull { it ->
                    it.username == username
                }
                if (matchedUser != null) {
                    userController.setLocalUserObject(matchedUser)
                    userController.setLocalUsername(username)
                    launchTaskList()
                } else {
                    showLoginError("Username: $username doesn't exist")
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                showLoginError("Network failed")
                t.printStackTrace()
                return
            }
        })
    }

    /**
     * Starts the [ListTasksActivity].
     *
     * @see ListTasksActivity
     */
    fun launchTaskList() {
        var intent = Intent(this, ListTasksActivity::class.java)
        startActivity(intent)
    }

    /**
     * Attempts to create new user with inputted username.
     * If username does not exist in Users index, opens the @EditUserActivity so that the new user
     * details can be added. Otherwise, display an error message indicating that the username is
     * taken.
     *
     * @param view
     * @see GenerateRetrofit
     */
    @OnClick(R.id.NewUserButton)
    fun newUserClicked(v: View) {
        val username: String = UsernameText.text.toString()
        GenerateRetrofit.generateRetrofit().getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                Log.i("network", response.body().toString())
                masterUserList.addAll(response.body() as ArrayList<User>)
                val matchedUser = masterUserList.firstOrNull { it ->
                    it.username == username
                }
                if (matchedUser != null) {
                    showLoginError("Username: $username already exists")
                } else {
                    userController.setLocalUsername("")
                    launchEditUserActivity(username)
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                showLoginError("Network failed")
                t.printStackTrace()
                return
            }
        })
    }

    /**
     * Starts the @EditUserActivity to allow user details to be entered for the new
     * user. Username is required for the new User object created by the activity.
     *
     * @param username
     * @see EditUserActivity
     */
    fun launchEditUserActivity(username: String) {
        var intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }

    @OnClick(R.id.ImageConnectionStatus)
    fun onTaskrImageClick() {
        // TODO: Implement clicking on the logo in part 5
    }


}
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * LoginActivity
 *
 * This class allows the user to login or create a new user, given a specified username
 */
class LoginActivity : AppCompatActivity() {

    @BindView(R.id.LoginButton)
    lateinit var LoginButton    : Button

    @BindView(R.id.NewUserButton)
    lateinit var NewUserButton  : Button

    @BindView(R.id.LoginErrorText)
    lateinit var LoginErrorText : TextView

    @BindView(R.id.UsernameText)
    lateinit var UsernameText   : EditText

    var MasterUserList : ArrayList<User> = ArrayList<User>()
    var userController: UserController = UserController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)
    }

    fun getConnectivityStatus() {

    }

    fun showLoginError(message: String) {
        Log.d("Username", message)
        LoginErrorText.text = message
    }

    @OnClick(R.id.LoginButton)
    fun LoginClicked(v: View) {

        val Username : String = UsernameText.text.toString()

        GenerateRetrofit.generateRetrofit().getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                Log.i("network", response.body().toString())
                MasterUserList.addAll(response.body() as ArrayList<User>)
                val exisiting = MasterUserList.firstOrNull {
                    it -> it.username == Username
                }
                if (exisiting != null) {
                    userController.setLocalUsername(Username)
                    launchTaskList()
                }
                else{
                    showLoginError("Username " + "doesn't exist")
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

    fun launchTaskList(){
        var intent = Intent(this, ListTasksActivity::class.java)
        startActivity(intent)
    }

    @OnClick(R.id.NewUserButton)
    fun NewUserClicked(v: View) {

        var Username : String = UsernameText.text.toString()

        GenerateRetrofit.generateRetrofit().getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                Log.i("network", response.body().toString())
                MasterUserList.addAll(response.body() as ArrayList<User>)
                val exisiting = MasterUserList.firstOrNull {
                    it -> it.username == Username
                }
                if (exisiting != null) {
                    showLoginError("Username: " + " Already exists")
                }
                else{
                    launchEditUserActivity(Username)
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

    fun launchEditUserActivity(username:String) {
        var intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }

    @OnClick(R.id.ImageConnectionStatus)
    fun onTaskrImageClick() {
        // TODO: Implement clicking on the logo in part 5
    }

}
package ca.ualberta.taskr

import android.content.Intent
import android.content.SharedPreferences
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)
    }

    fun CheckIfUsernameExists(username: String) : Boolean {

        GenerateRetrofit.generateRetrofit().getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                Log.i("network", response.body().toString())
                MasterUserList.addAll(response.body() as ArrayList<User>)

            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
                return
            }
        })

        for (user in MasterUserList) {
            // TODO: This is t/otally not how this should be done but im doing it anyways yolo
            if (user.username == username) return true
        }
        return false
    }

    fun getConnectivityStatus() {

    }

    fun showLoginError(message: String) {
        Log.d("Username", message)
        LoginErrorText.setText(message)
    }

    @OnClick(R.id.LoginButton)
    fun LoginClicked(v: View) {

        var Username : String = UsernameText.text.toString()

        if (CheckIfUsernameExists(Username)) {
            showLoginError("Username: " + " Already exists")
        }
        else {
            showLoginError("Username: " + " Doesnt exist")
            return
        }


        android.util.Log.d("CLICK", "Login button clicked")

        AddUserToSharedPreferences(Username)

        var intent = Intent(this, ListTasksActivity::class.java)
        startActivity(intent)

    }

    @OnClick(R.id.NewUserButton)
    fun NewUserClicked(v: View) {

        var Username : String = UsernameText.text.toString()

        if (CheckIfUsernameExists(Username)) {
            showLoginError("Username: " + " Already exists")
            return
        }
        else {
            showLoginError("Username: " + " Doesnt exist")
        }


        android.util.Log.d("CLICK", "NewUser button clicked")

        AddUserToSharedPreferences(Username)
        var intent = Intent(this, EditUserActivity::class.java)

        startActivity(intent)
    }

    @OnClick(R.id.ImageConnectionStatus)
    fun onTaskrImageClick() {
        // TODO: Implement clicking on the logo in part 5
    }

    fun AddUserToSharedPreferences(username : String){
        lateinit var editor : SharedPreferences.Editor
        editor = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE).edit()

        editor.putString("Username", username)
        editor.apply()
    }
}
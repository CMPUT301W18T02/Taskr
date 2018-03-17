package ca.ualberta.taskr

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.OnClick

class LoginActivity : AppCompatActivity() {

    //TODO Hook up items
    lateinit var LoginButton    : Button
    lateinit var NewUserButton  : Button
    lateinit var LoginErrorText : TextView
    lateinit var UsernameText   : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)
    }

    fun CheckIfUsernameExists(username: String) {

    }

    fun getConnectivityStatus() {

    }

    fun showLoginError(message: String) {

    }

    @OnClick(R.id.LoginButton)
    fun LoginClicked(v: View) {
        android.util.Log.d("CLICK", "Login button clicked")
    }

    @OnClick(R.id.NewUserButton)
    fun NewUserClicked(v: View) {
        android.util.Log.d("CLICK", "NewUser button clicked")
        val intent = Intent(this, ViewTaskActivity::class.java)
        startActivity(intent)
    }

    @OnClick(R.id.ImageConnectionStatus)
    fun onTaskrImageClick() {

    }

    fun bundleActivity() {

    }
}
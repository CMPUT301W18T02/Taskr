package ca.ualberta.taskr

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.ViewById

class LoginActivity : AppCompatActivity() {

    @ViewById lateinit var LoginButton    : Button
    @ViewById lateinit var NewUserButton  : Button
    @ViewById lateinit var LoginErrorText : TextView
    @ViewById lateinit var UsernameText   : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun CheckIfUsernameExists(username: String) {

    }

    fun getConnectivityStatus() {

    }

    fun showLoginError(message: String) {

    }

    @Click(R.id.LoginButton)
    fun LoginClicked(v: View) {
        android.util.Log.d("CLICK", "Login button clicked")

    }

    @Click(R.id.NewUserButton)
    fun NewUserClicked(v: View) {
        android.util.Log.d("CLICK", "NewUser button clicked")
        val intent = Intent(this, ViewTaskActivity::class.java)
        startActivity(intent)
    }

    @Click(R.id.ImageConnectionStatus)
    fun onTaskrImageClick() {

    }

    fun bundleActivity() {

    }
}
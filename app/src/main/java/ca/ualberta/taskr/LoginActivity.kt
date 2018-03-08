package ca.ualberta.taskr

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.androidannotations.annotations.Click
import org.androidannotations.annotations.ViewById

class LoginActivity : AppCompatActivity() {

    @ViewById val LoginButton = null;

    @ViewById val NewUserButton = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    @Click(R.id.LoginButton)
    fun LoginClicked() {

    }

    @Click(R.id.NewUserButton)
    fun NewUserClicked() {

    }
}
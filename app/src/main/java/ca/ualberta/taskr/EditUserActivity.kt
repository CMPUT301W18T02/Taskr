package ca.ualberta.taskr

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.androidannotations.annotations.Click

class EditUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
    }

    @Click(R.id.ApplyChangesButton)
    fun onApplyChangesClicked() {

    }

    @Click(R.id.ProfileImageButton)
    fun onPhotoClicked() {

    }
}

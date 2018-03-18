package ca.ualberta.taskr

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit


class EditUserActivity : AppCompatActivity() {

    lateinit var CurrentUser: User
    var IsNewUser = false
    lateinit var Username: String
    @BindView(R.id.UserSurnameText)
    lateinit var UserSurnameText: EditText

    @BindView(R.id.UserPhoneNumberText)
    lateinit var UserPhoneNumberText: EditText

    @BindView(R.id.UserEmailText)
    lateinit var UserEmailText: EditText

    @BindView(R.id.ApplyChangesButton)
    lateinit var ApplyChangesButton: Button

    @BindView(R.id.EditUserErrorTextView)
    lateinit var EditUSerErrorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        if (IsNewUser) ApplyChangesButton.setText("Create User")
        else ApplyChangesButton.setText("Edit User")
    }

    fun DisplayErrorMessage(message: String) {
    }

    fun CheckNameFormatting(name: String): Boolean {
        return false
    }

    fun CheckPhoneNumberFormatting(PhoneNumber: String): Boolean {
        return false
    }

    fun CheckEmailFormatting(Email: String): Boolean {
        return false
    }

    fun onApplyChangesClicked() {
        lateinit var Name: String
        lateinit var PhoneNumber: String
        lateinit var Email: String

        Name = UserSurnameText.text.toString()
        PhoneNumber = UserPhoneNumberText.text.toString()
        Email = UserEmailText.text.toString()

        if (CheckNameFormatting(Name)) {
            DisplayErrorMessage("Invalid Name")
            return
        }

        if (CheckPhoneNumberFormatting(PhoneNumber)) {
            DisplayErrorMessage("Invalid PhoneNumber")
            return
        }

        if (CheckEmailFormatting(Email)) {
            DisplayErrorMessage("Invalid Email")
            return
        }
        //TODO: GET USERNAME FROM SHARED PREFS
        //TODO: Images
        CurrentUser = User(Name, PhoneNumber,null,Email, username = "blah")

    }

    fun UpdateUser(user: User) {


        if (IsNewUser) {

        } else {

        }
    }

    fun onPhotoClicked() {

    }
}

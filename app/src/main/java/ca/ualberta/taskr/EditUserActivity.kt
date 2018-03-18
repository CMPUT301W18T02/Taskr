package ca.ualberta.taskr

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * EditUserActivity
 * This Activity allows for the creation and editing of a user to occur
 */
class EditUserActivity : AppCompatActivity() {

    lateinit var CurrentUser : User
    var IsNewUser = false
    lateinit var Username : String
    @BindView(R.id.UserSurnameText)
    lateinit var UserSurnameText : EditText

    @BindView(R.id.UserPhoneNumberText)
    lateinit var UserPhoneNumberText : EditText

    @BindView(R.id.UserEmailText)
    lateinit var UserEmailText : EditText

    @BindView(R.id.ApplyChangesButton)
    lateinit var ApplyChangesButton: Button

    @BindView(R.id.EditUserErrorTextView)
    lateinit var EditUSerErrorTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        ButterKnife.bind(this)

        if (IsNewUser) ApplyChangesButton.setText("Create User")
        else ApplyChangesButton.setText("Edit User")

        var editor = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE)
        Username = editor.getString("Username", null)
    }

    fun DisplayErrorMessage(message : String) {
    }

    fun CheckNameFormatting(name : String) : Boolean {
        return false
    }

    fun CheckPhoneNumberFormatting(PhoneNumber : String) : Boolean {
        return false
    }

    fun CheckEmailFormatting(Email : String) : Boolean {
        return false
    }

    @OnClick(R.id.ApplyChangesButton)
    fun onApplyChangesClicked() {
        lateinit var Name : String
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

        CurrentUser = User(Name, PhoneNumber, null, Email, Username)
        UpdateUser(CurrentUser)
    }

    fun UpdateUser(user : User) {
        if (IsNewUser) {
            GenerateRetrofit.generateRetrofit().createUser(user)
        }
        else {
            lateinit var id : ElasticsearchID
            GenerateRetrofit.generateRetrofit().getUserID(Query.userQuery(user.username)).enqueue(object : Callback<ElasticsearchID> {
                override fun onResponse(call: Call<ElasticsearchID>, response: Response<ElasticsearchID>) {
                    Log.i("network", response.body().toString())
                    id = response.body() as ElasticsearchID
                    GenerateRetrofit.generateRetrofit().updateUser(id.toString(), user)
                }

                override fun onFailure(call: Call<ElasticsearchID>, t: Throwable) {
                    Log.e("network", "Network Failed!")
                    t.printStackTrace()
                    return
                }
            })

        }

        var intent = Intent(this, ListTasksActivity::class.java)
        startActivity(intent)
    }

    fun onPhotoClicked() {

    }
}

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
import ca.ualberta.taskr.controllers.UserController
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

    lateinit var CurrentUser: User
    private var isNewUser = false
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

    var userController : UserController = UserController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        ButterKnife.bind(this)

        Username = userController.getLocalUserName()

        isNewUser = (Username == "")

        if (isNewUser) ApplyChangesButton.setText("Create User")
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

    @OnClick(R.id.ApplyChangesButton)
    fun onApplyChangesClicked() {
        val name: String = UserSurnameText.text.toString()
        val phoneNumber: String = UserPhoneNumberText.text.toString()
        val email: String = UserEmailText.text.toString()

        if (CheckNameFormatting(name)) {
            DisplayErrorMessage("Invalid Name")
            return
        }

        if (CheckPhoneNumberFormatting(phoneNumber)) {
            DisplayErrorMessage("Invalid PhoneNumber")
            return
        }

        if (CheckEmailFormatting(email)) {
            DisplayErrorMessage("Invalid Email")
            return
        }

        CurrentUser = User(name, phoneNumber, null, email, Username)
        UpdateUser(CurrentUser)
    }

    fun UpdateUser(user : User) {
        if (isNewUser) {
            GenerateRetrofit.generateRetrofit().createUser(user).enqueue(object: Callback<Void> {
                override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                    openListTasksActivity()
                }

                override fun onFailure(call: Call<Void>?, t: Throwable?) {
                    Log.e("network", "Network Failed!")
                }
            })
        }
        else {
            lateinit var id : ElasticsearchID
            GenerateRetrofit.generateRetrofit().getUserID(Query.userQuery(user.username)).enqueue(object : Callback<ElasticsearchID> {
                override fun onResponse(call: Call<ElasticsearchID>, response: Response<ElasticsearchID>) {
                    Log.i("network", response.body().toString())
                    id = response.body() as ElasticsearchID
                    GenerateRetrofit.generateRetrofit().updateUser(id.toString(), user)
                    openListTasksActivity()
                }

                override fun onFailure(call: Call<ElasticsearchID>, t: Throwable) {
                    Log.e("network", "Network Failed!")
                    t.printStackTrace()
                    return
                }
            })
        }
    }

    fun openListTasksActivity(){
        var intent = Intent(this, ListTasksActivity::class.java)
        startActivity(intent)
    }

    fun onPhotoClicked() {

    }
}

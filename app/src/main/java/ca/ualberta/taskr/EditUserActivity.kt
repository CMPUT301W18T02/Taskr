package ca.ualberta.taskr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.controllers.UserController
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.ElasticsearchID
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.Query
import kotlinx.android.synthetic.main.activity_edit_user.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.provider.MediaStore
import android.graphics.Bitmap
import ca.ualberta.taskr.util.PhotoConversion


/**
 * EditUserActivity
 * This Activity allows for the creation and editing of a user to occur
 */
class EditUserActivity : AppCompatActivity() {

    lateinit var CurrentUser: User
    private var isNewUser = false
    lateinit var Username: String
    val REQUEST_IMAGE_CAPTURE = 1

    @BindView(R.id.ProfileImageButton)
    lateinit var profileImageButton: ImageButton

    @BindView(R.id.UserSurnameText)
    lateinit var userSurnameText: EditText

    @BindView(R.id.UserPhoneNumberText)
    lateinit var userPhoneNumberText: EditText

    @BindView(R.id.UserEmailText)
    lateinit var userEmailText: EditText

    @BindView(R.id.ApplyChangesButton)
    lateinit var ApplyChangesButton: Button

    @BindView(R.id.EditUserErrorTextView)
    lateinit var editUserErrorTextView: TextView

    var encodedProfileImage:String = ""

    var userController: UserController = UserController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)
        ButterKnife.bind(this)

        Username = userController.getLocalUserName()

        isNewUser = (Username == "")

        if (isNewUser) {
            ApplyChangesButton.text = "Create User"
            Username = intent.getStringExtra("username")
        }
        else {
            // TODO Populate user data
            ApplyChangesButton.text = "Edit User"
            val oldUserObject = userController.getLocalUserObject()
            if (oldUserObject != null){
                Log.e("User", oldUserObject.name)
                userSurnameText.setText(oldUserObject.name)
                userEmailText.setText(oldUserObject.email)
                userPhoneNumberText.setText(oldUserObject.phoneNumber)
            }
        }

    }


    private fun displayErrorMessage(message: String) {
        editUserErrorTextView.text = message
    }

    fun CheckPhoneNumberFormatting(PhoneNumber: String): Boolean {
        // TODO
        return false
    }

    fun CheckEmailFormatting(Email: String): Boolean {
        // TODO
        return false
    }

    /**
     * Apply changes to a user with error checking
     */
    @OnClick(R.id.ApplyChangesButton)
    fun onApplyChangesClicked() {
        val name: String = userSurnameText.text.toString()
        val phoneNumber: String = userPhoneNumberText.text.toString()
        val email: String = userEmailText.text.toString()

        if (CheckPhoneNumberFormatting(phoneNumber)) {
            displayErrorMessage("Invalid PhoneNumber")
            return
        }

        if (CheckEmailFormatting(email)) {
            displayErrorMessage("Invalid Email")
            return
        }

        CurrentUser = User(name, phoneNumber, encodedProfileImage, email, Username)
        UpdateUser(CurrentUser)
    }

    /**
     * Update the user using async network calls
     *
     * @param user The user object to get updated
     */
    fun UpdateUser(user: User) {
        if (isNewUser) {
            GenerateRetrofit.generateRetrofit().createUser(user).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                    userController.setLocalUsername(Username)
                    userController.setLocalUserObject(user)
                    openListTasksActivity()
                }

                override fun onFailure(call: Call<Void>?, t: Throwable?) {
                    Log.e("network", "Network Failed on creation!")
                }
            })
        } else {
            CachingRetrofit(this).updateUser(object : ca.ualberta.taskr.models.elasticsearch.Callback<Boolean> {
                override fun onResponse(response: Boolean, responseFromCache: Boolean) {
                    //TODO offline functionality
                    userController.setLocalUsername(Username)
                    userController.setLocalUserObject(user)
                    openListTasksActivity()

                }
            }).execute(user)
        }
    }

    /**
     * Open the list tasks activity
     */
    fun openListTasksActivity() {
        var intent = Intent(this, ListTasksActivity::class.java)
        startActivity(intent)
    }

    @OnClick(R.id.ProfileImageButton)
    fun onPhotoClicked() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            val imageBitmap = extras!!.get("data") as Bitmap
            profileImageButton.setImageBitmap(imageBitmap)
            encodedProfileImage = PhotoConversion.getPhotoString(imageBitmap)
        }
    }
}

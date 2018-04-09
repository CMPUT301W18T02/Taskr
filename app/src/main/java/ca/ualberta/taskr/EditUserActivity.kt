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
 * This Activity allows for the creation and editing of a [User]
 *
 * @author eyesniper2
 * @author MichaelSteer
 *
 * @property CurrentUser [User] object
 * @property isNewUser True if inputted details are for new user, false otherwise.
 * @property Username Inputted username from [LoginActivity]
 * @property REQUEST_IMAGE_CAPTURE Request code.
 * @property profileImageButton Button for displaying user profile image.
 * @property userSurnameText TextView for displaying user first and last name.
 * @property userPhoneNumberText TextView for displaying user phone number.
 * @property userEmailText TextView for displaying user email.
 * @property ApplyChangesButton Button for confirming changes and terminating activity.
 * @property editUserErrorTextView TextView for displaying any errors in user input.
 * @property encodedProfileImage String-encoded user profile image.
 * @property userController [UserController] for saving username/[User] object to shared preferences.
 *
 * @see [LoginActivity]
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

    /**
     * Populates fields with user information if a [User] object is provided to the
     * activity (i.e. user is editing their information).
     *
     * @param savedInstanceState
     */
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
                if (oldUserObject.profilePicture != null &&
                        oldUserObject.profilePicture.isNotEmpty()){
                    profileImageButton.setImageBitmap(PhotoConversion.getBitmapFromString(oldUserObject.profilePicture))
                }
            }
        }
    }


    /**
     * Updates view displaying error message describing invalid user info.
     *
     * @param message String describing invalid user info.
     */
    private fun displayErrorMessage(message: String) {
        editUserErrorTextView.text = message
    }

    /**
     * Returns true if phone number is valid.
     *
     * @param phoneNumber Inputted phone number string.
     * @return [Boolean] True if valid phone number, false otherwise.
     */
    fun CheckPhoneNumberFormatting(PhoneNumber: String): Boolean {
        // TODO
        return false
    }

    /**
     * Returns true if email address is valid.
     *
     * @param Email Inputted email address string.
     * @return [Boolean] True if valid email address, false otherwise.
     */
    fun CheckEmailFormatting(Email: String): Boolean {
        // TODO
        return false
    }

    /**
     * Apply inputted user information to [User] object, then update the current user
     * information in saved preferences.
     * Inputted info is checked and an error message is displayed instead if some of the
     * information is invalid.
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
     * Update the current user's information on the server using async network calls.
     * If new user, the new [User] object to the server. If successful, current username and [User]
     * object are stored to shared preferences.
     * If existing user, update [User] object on server and the user info is similarly saved to
     * shared preferences. Otherwise, updated [User] object is stored locally until server
     * connectivity is established.
     *
     * @param user The user object to get updated
     * @see [GenerateRetrofit]
     * @see [CachingRetrofit]
     * @see [UserController]
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
     * Open the [ListTasksActivity].
     *
     * @see [ListTasksActivity]
     */
    fun openListTasksActivity() {
        var intent = Intent(this, ListTasksActivity::class.java)
        startActivity(intent)
    }

    /**
     * Start's [MediaStore] image capture activity for requesting photo from user storage.
     *
     * @see [MediaStore]
     */
    @OnClick(R.id.ProfileImageButton)
    fun onPhotoClicked() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    /**
     * Obtains image provided by user via [MediaStore] image capture activity, updates
     * profile image on screen to provided image, then encodes the photo as a string.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     *
     * @see [MediaStore]
     * @see [PhotoConversion]
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data==null) return
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            val imageBitmap = extras!!.get("data") as Bitmap
            profileImageButton.setImageBitmap(imageBitmap)
            encodedProfileImage = PhotoConversion.getPhotoString(imageBitmap)
        }
    }
}

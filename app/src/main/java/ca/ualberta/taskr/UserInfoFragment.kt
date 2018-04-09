package ca.ualberta.taskr

import android.app.ActionBar
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.util.PhotoConversion


/**
 * A fragment for displaying user information when that user's username is clicked somewhere
 * in the app.
 *
 * @author jtbakker
 * @property user The [User] object corresponding to the clicked username
 * @property username [TextView] displaying username
 * @property userEmail [TextView] displaying user email address
 * @property userPhone [TextView] displaying user phone number
 */
class UserInfoFragment : DialogFragment() {

    private lateinit var user : User
    @BindView(R.id.userProfileImagePopup)
    lateinit var userPhoto : ImageView
    @BindView(R.id.usernamePopup)
    lateinit var username : TextView

    @BindView(R.id.userEmailAddressPopup)
    lateinit var userEmail : TextView

    @BindView(R.id.userPhoneNumberPopup)
    lateinit var userPhone : TextView

    /**
     * Populates text fields with user information.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_user_info, container, false)
        ButterKnife.bind(this, view)

        if (arguments != null) {
            var strUser = arguments.getString("USER")
            user = GenerateRetrofit.generateGson().fromJson(strUser, User::class.java)
            var profilePicString = user.profilePicture
            if (profilePicString != null && profilePicString.isNotEmpty()) {
                userPhoto.setImageBitmap(PhotoConversion.getBitmapFromString(profilePicString))
            }
            username.text = user.name
            userEmail.text = user.email
            userPhone.text = user.phoneNumber
        }

        return view
    }

    /**
     * Sets fragment's width to match screen width.
     */
    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
    }

    companion object {

        private val ARG_USER = "USER"

        /**
        * Factory method for creating instance of UserBidFragment given a User object.
        *
        * @param user
        * @return fragment
        */
        fun newInstance(user: User): UserInfoFragment {
            val fragment = UserInfoFragment()
            val args = Bundle()
            val strUser = GenerateRetrofit.generateGson().toJson(user, User::class.java)
            args.putString(ARG_USER, strUser)
            fragment.arguments = args
            return fragment
        }
    }
}

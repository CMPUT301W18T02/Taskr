package ca.ualberta.taskr

import android.app.ActionBar
import android.app.DialogFragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit


class UserInfoFragment : DialogFragment() {

    private lateinit var user : User
    @BindView(R.id.usernamePopup)
    lateinit var username : TextView
    @BindView(R.id.userEmailAddressPopup)
    lateinit var userEmail : TextView
    @BindView(R.id.userPhoneNumberPopup)
    lateinit var userPhone : TextView

    private var mListener: UserFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_user_info, container, false)
        ButterKnife.bind(this, view)

        if (arguments != null) {
            var strUser = arguments.getString("USER")
            user = GenerateRetrofit.generateGson().fromJson(strUser, User::class.java)

            username.text = user.name
            userEmail.text = user.email
            userPhone.text = user.phoneNumber
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UserFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface UserFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        private val ARG_USER = "USER"

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

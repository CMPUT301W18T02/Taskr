package ca.ualberta.taskr

import android.os.Bundle
import android.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick


/**
 * Fragment for displaying an error message.
 *
 * @author jtbakker
 * @property message [String] of the displayed error message.
 * @property messageView [TextView] for the error message.
 * @see [DialogFragment]
 */
class ErrorDialogFragment : DialogFragment() {
    private lateinit var message : String
    @BindView(R.id.errorMessage)
    lateinit var messageView : TextView

    /**
     * Populates text fields.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_error_dialog, container, false)
        ButterKnife.bind(this, view)
        if (arguments != null) {
            message = arguments?.getString("MESSAGE", "Something went wrong?") as String
            messageView.text = message
        }
        return view
    }

    companion object {
        private val ARG_MESSAGE = "MESSAGE"

        /**
         * Returns instance of [ErrorDialogFragment] given an error message string.
         *
         * @param message [String] of the error message.
         * @return fragment
         */
        fun newInstance(message : String) : ErrorDialogFragment {
            val fragment = ErrorDialogFragment()
            val args = Bundle()
            args.putString(ARG_MESSAGE, message)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * Closes the fragment when "OK" button is clicked.
     *
     * @param view
     */
    @OnClick(R.id.errorDismiss)
    fun dismiss(view : View) {
        this.dismiss()
    }
}
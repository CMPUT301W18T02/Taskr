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


class ErrorDialogFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private lateinit var message : String
    @BindView(R.id.errorMessage)
    lateinit var messageView : TextView


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

        fun newInstance(message : String) : ErrorDialogFragment {
            val fragment = ErrorDialogFragment()
            val args = Bundle()
            args.putString(ARG_MESSAGE, message)
            fragment.arguments = args
            return fragment
        }
    }

    @OnClick(R.id.errorDismiss)
    fun dismiss(view : View) {
        this.dismiss()
    }
}

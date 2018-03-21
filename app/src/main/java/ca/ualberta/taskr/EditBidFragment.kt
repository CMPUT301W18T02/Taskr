package ca.ualberta.taskr

import android.app.ActionBar
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import java.lang.Exception


/**
 * EditBidFragment class
 *
 * Allows task providers to add/edit Bids on some Task.
 */
class EditBidFragment : DialogFragment() {

    private lateinit var displayBid : Bid
    @BindView(R.id.enterAmountEdit)
    lateinit var enterAmountView : EditText
    @BindView(R.id.fragmentTitle)
    lateinit var fragmentTitle : TextView
    private var mListener: EditBidFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            if (arguments.getString("DISPLAYBID") != null) {
                var strBid = arguments.getString("DISPLAYBID")
                fragmentTitle.text = getString(R.string.fragment_edit_bid)
                displayBid = GenerateRetrofit.generateGson().fromJson(strBid, Bid::class.java)
            }
        }
    }

    /**
     * Creates the view for EditBidFragment's layout, then updates displayed bid amount with
     * value obtained from a provided Bid object. If no Bid was provided, no update is made.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment

        var view = inflater.inflate(R.layout.fragment_edit_bid, container, false)
        ButterKnife.bind(this, view)
        try {
            if (displayBid.amount > 0) {
                enterAmountView.setText(displayBid.amount.toString())
            }
        } catch (e : UninitializedPropertyAccessException) {
            // If displayBid not initialized, then no Bid object was provided by
            // container activity.
            Log.e("DisplayBid Object", "No Bid object provided")
        }
        return view
    }

    /**
     * Update layout width to MATCH_PARENT so that fragment width expands to fit screen width.
     */
    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
    }

    /**
     * Default onAttach function.
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is EditBidFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    /**
     * Default onDetach function
     */
    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * Defines stub for bid add/update methods.
     */
    interface EditBidFragmentInteractionListener {
        fun bidUpdate(bidAmount : Double, originalBid : Bid)
        fun bidAdd(bidAmount : Double)
    }

    companion object {
        private val ARG_DISPLAYBID = "DISPLAYBID"

        /**
         * Factory method for creating instance of EditBidFragment given a Bid object.
         */
        fun newInstance(bid: Bid): EditBidFragment {
            val fragment = EditBidFragment()
            val args = Bundle()
            val strBid = GenerateRetrofit.generateGson().toJson(bid, Bid::class.java )
            args.putString(ARG_DISPLAYBID, strBid)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * Cancels adding/editing bid by closing EditBidFragment.
     */
    @OnClick(R.id.cancel)
    fun cancel(view : View) {
        this.dismiss()
    }

    /**
     * Verifies that inputted bid amount is a valid Double, then passes it back to container activity
     * using bidUpdate to allow bid to be created/edited. Closes EditBidFragment afterwards.
     */
    @OnClick(R.id.confirm)
    fun confirm(view : View) {
        var inputAmount : Double
        var inputAmountString = enterAmountView.text.toString()
        try {
            inputAmount = inputAmountString.toDouble()
        } catch (e : Exception) {
            // Exit function if inputted bid amount is invalid.
            return
        }
        if (inputAmount > 0) {
            try {
                // If displayBid is initialized, then Provider is updating a bid.
                mListener?.bidUpdate(inputAmount, displayBid)
            } catch (e : UninitializedPropertyAccessException) {
                // If displayBid is not initialized, then Provider is creating a new bid.
                mListener?.bidAdd(inputAmount)
            }
            this.dismiss()
        }
    }
}

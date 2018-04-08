package ca.ualberta.taskr

import android.app.ActionBar
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.models.Bid
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit


/**
 * Fragment that allows Task Requesters to accept or decline a bid on one of tasks.
 *
 * @author jtbakker
 * @property bidAmountView TextView displaying bid amount.
 * @property bidUsernameView TextView displaying bid owner.
 * @property mListener Allows this fragment's interface methods to be called in @ViewTaskActivity.
 * @see [DialogFragment]
 */
class AcceptBidFragment : DialogFragment() {

    private lateinit var displayBid: Bid
    @BindView(R.id.requesterBidAmount)
    lateinit var bidAmountView : TextView
    @BindView(R.id.requesterBidUsername)
    lateinit var bidUsernameView : TextView
    private var mListener: AcceptBidFragmentInteractionListener? = null

    /**
     * Creates the view for AcceptBidFragment's layout, then updates displayed Bid attributes with
     * those obtained from a provided Bid object.
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_accept_bid, container, false)
        ButterKnife.bind(this, view)
        if (arguments != null) {
            var strBid = arguments.getString("DISPLAYBID")
            displayBid = GenerateRetrofit.generateGson().fromJson(strBid, Bid::class.java)
        }
        // Update Bid attribute views using received Bid.
        bidAmountView.text = String.format(bidAmountView.text.toString(), displayBid.amount)
        bidUsernameView.text = String.format(bidUsernameView.text.toString(), displayBid.owner)
        return view
    }

    /**
     * Update fragment width to fit screen width.
     */
    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
    }

    /**
     * Default onAttach function for fragments.
     *
     * @param context
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is AcceptBidFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    /**
     * Default onDetach function for fragments.
     */
    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * Defines method stubs for accept/decline bid methods.
     */
    interface AcceptBidFragmentInteractionListener {
        fun declinedBid(bid: Bid)
        fun acceptedBid(bid: Bid)
    }

    companion object {
        private val ARG_DISPLAYBID = "DISPLAYBID"

        /**
         * Factory method for creating instance of AcceptBidFragment given a Bid object.
         *
         * @param bid
         * @return fragment
         */
        fun newInstance(bid: Bid): AcceptBidFragment {
            val fragment = AcceptBidFragment()
            val args = Bundle()
            val strBid = GenerateRetrofit.generateGson().toJson(bid, Bid::class.java )
            args.putString(ARG_DISPLAYBID, strBid)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * Cancels accept/decline of bid by closing AcceptBidFragment.
     *
     * @param view
     */
    @OnClick(R.id.requesterCancel)
    fun cancel(view : View) {
        this.dismiss()
    }

    /**
     * Declines bid by calling declineBid method in [ViewActivity],
     * then closes AcceptBidFragment.
     *
     * @param view
     * @see [ViewTaskActivity]
     */
    @OnClick(R.id.requesterDecline)
    fun decline(view : View) {
        mListener?.declinedBid(displayBid)
        this.dismiss()
    }

    /**
     * Accepts bid by calling acceptBid method in [ViewTaskActivity]
     * then closes AcceptBidFragment.
     *
     * @param view
     * @see [ViewTaskActivity]
     */
    @OnClick(R.id.requesterAccept)
    fun accept(view : View) {
        mListener?.acceptedBid(displayBid)
        this.dismiss()
    }
}
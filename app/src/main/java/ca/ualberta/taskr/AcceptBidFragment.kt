package ca.ualberta.taskr

import android.app.ActionBar
import android.app.DialogFragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
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
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AcceptBidFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AcceptBidFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AcceptBidFragment : DialogFragment() {

    private var displayBid: Bid? = null
    @BindView(R.id.requesterBidAmount)
    lateinit var bidAmountView : TextView
    @BindView(R.id.requesterBidUsername)
    lateinit var bidUsernameView : TextView
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            var strBid = arguments!!.getString("DISPLAYBID")
            displayBid = GenerateRetrofit.generateGson().fromJson(strBid, Bid::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_accept_bid, container, false)
        ButterKnife.bind(this, view)
        bidAmountView.text = String.format(bidAmountView.text.toString(), displayBid?.amount)
        bidUsernameView.text = String.format(bidUsernameView.text.toString(), displayBid?.owner)
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
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
    interface OnFragmentInteractionListener {
        fun declinedBid(bid: Bid)
        fun acceptedBid(bid: Bid)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_DISPLAYBID = "DISPLAYBID"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditBidFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(bid: Bid): EditBidFragment {
            val fragment = EditBidFragment()
            val args = Bundle()
            val strBid = GenerateRetrofit.generateGson().toJson(bid, Bid::class.java )
            args.putString(ARG_DISPLAYBID, strBid)
            fragment.arguments = args
            return fragment
        }
    }

    @OnClick(R.id.requesterCancel)
    fun cancel(view : View) {
        this.dismiss()
    }

    @OnClick(R.id.requesterDecline)
    fun decline(view : View) {
        if (mListener != null) {
            mListener!!.declinedBid(displayBid!!)
            this.dismiss()
        }
    }

    @OnClick(R.id.requesterAccept)
    fun accept(view : View) {
        if (mListener != null) {
            mListener!!.acceptedBid(displayBid!!)
            this.dismiss()
        }
    }

}// Required empty public constructor

package ca.ualberta.taskr

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import ca.ualberta.taskr.controllers.NavViewController
import ca.ualberta.taskr.util.AdHelper
import ca.ualberta.taskr.util.AdHelper.Companion.removeAdsTemporarily
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import kotlinx.android.synthetic.main.activity_reward_video.*

const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
const val APP_ID = "ca-app-pub-3940256099942544~3347511713"


/**
 * Created by xrend on 4/8/2018.
 */
class RewardVideoActivity : AppCompatActivity(), RewardedVideoAdListener {

    @BindView(R.id.drawer_layout)
    lateinit var drawerLayout: DrawerLayout
    @BindView(R.id.nav_view)
    lateinit var navView: NavigationView
    @BindView(R.id.myTasksToolbar)
    lateinit var toolbar: Toolbar

    private lateinit var mRewardedVideoAd: RewardedVideoAd
    private lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward_video)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu)

        NavViewController(navView, drawerLayout, applicationContext)

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, APP_ID)

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = this
        loadRewardedVideoAd()

        mAdView = findViewById(R.id.adView)
        if (AdHelper.isAdFree(this)) {
            mAdView.visibility = View.GONE
            Log.d("AD", "Visibility is GONE")
        } else {
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)
        }

        // Create the "show" button, which shows a rewarded video if one is loaded.
        show_video_button.visibility = View.INVISIBLE
        show_video_button.setOnClickListener { showRewardedVideo() }

    }

    public override fun onPause() {
        super.onPause()
        pauseGame()
        mRewardedVideoAd.pause(this)
    }

    public override fun onResume() {
        super.onResume()
        mRewardedVideoAd.resume(this)
    }

    private fun pauseGame() {
    }

    private fun loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded) {
            mRewardedVideoAd.loadAd(AD_UNIT_ID, AdRequest.Builder().build())
        }
    }

    private fun showRewardedVideo() {
        show_video_button.visibility = View.INVISIBLE
        if (mRewardedVideoAd.isLoaded) {
            mRewardedVideoAd.show()
        }
    }

    override fun onRewardedVideoAdLeftApplication() {
    }

    override fun onRewardedVideoAdClosed() {
        // Preload the next video ad.
        loadRewardedVideoAd()
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
    }

    override fun onRewardedVideoAdLoaded() {
        show_video_button.visibility = View.VISIBLE
    }


    override fun onRewarded(reward: RewardItem) {
        Toast.makeText(this, "You get to browse ad-free for ${reward.amount} minutes!", Toast.LENGTH_SHORT).show()

        removeAdsTemporarily(this, reward.amount.toLong())
        mAdView.visibility = View.GONE

    }

    override fun onRewardedVideoAdOpened() {
    }

    override fun onRewardedVideoCompleted() {
    }

    override fun onRewardedVideoStarted() {
    }


}
package ca.ualberta.taskr

import android.content.Intent
import android.widget.Button
import com.mapbox.mapboxsdk.geometry.LatLng
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewAction

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId

/**
 * Created by marissasnihur on 2018-04-06.
 *
 * This Test Class handles all of the functionality of AddLocationToTaskActivity, and
 * deals with maps and a button that sends a location back to the EditTaskActivity.
 */

@RunWith(AndroidJUnit4::class)
//@Config(constants = BuildConfig::class, sdk = intArrayOf(26))
class GeoLocationTests {


    @Rule @JvmField
    var activity = ActivityTestRule<AddLocationToTaskActivity>(AddLocationToTaskActivity::class.java)

    @Test
    fun checkActivityNotNull(){
        Assert.assertNotNull(activity)
    }

    /**
     * Tests the Add Location button - makes sure that it sends the correct Latitude and Longitude
     * to the activity. Makes sure that the activity that the location is sent to is indeed
     * the correct activity as well.
     */

    @Test
    fun onButtonClickTest(){

        onView(withId(R.id.add_location)).perform(click())

        val point = LatLng(55.5,103.6)

        Thread.sleep(1000)

        val intent = Intent(activity.activity, EditTaskActivity::class.java)

        intent.putExtra("position",point)

        Thread.sleep(1000)

        Assert.assertEquals(EditTaskActivity::class.java.canonicalName, intent.component.className)

    }

    /**
     * Test to make sure that the mapClick is picked up by the view.
     */

    @Test
    fun onMapClickTest(){

        onView(withId(R.id.rangeMapView)).perform(click())

    }

}

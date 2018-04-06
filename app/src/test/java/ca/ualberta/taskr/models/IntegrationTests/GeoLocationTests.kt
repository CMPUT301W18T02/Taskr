package ca.ualberta.taskr.models.IntegrationTests

import android.content.Intent
import android.widget.Button
import ca.ualberta.taskr.*
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.synthetic.main.activity_add_location_to_task.view.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Created by marissasnihur on 2018-04-06.
 *
 * This Test Class handles all of the functionality of AddLocationToTaskActivity, and
 * deals with maps and a button that sends a location back to the EditTaskActivity.
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(26))
class GeoLocationTests {

    private lateinit var activity: AddLocationToTaskActivity
    private lateinit var button: Button

    /**
     * Sets up all of the variables needed by the Test Class.
     */

    @Before
    fun setUp(){

        activity = Robolectric.setupActivity(AddLocationToTaskActivity::class.java)

        button = activity.findViewById<Button>(R.id.add_location)
    }

    /**
     * Tests the Add Location button - makes sure that it sends the correct Latitude and Longitude
     * to the activity. Makes sure that the activity that the location is sent to is indeed
     * the correct activity as well.
     */

    @Test
    fun onButtonClick(){
        val point = LatLng(55.5,103.6)

        button.performClick()

        Thread.sleep(1000)

        val intent = Intent(activity, EditTaskActivity::class.java)

        intent.putExtra("position",point)

        Thread.sleep(1000)

        Assert.assertEquals(EditTaskActivity::class.java.canonicalName, intent.component.className)

    }

}

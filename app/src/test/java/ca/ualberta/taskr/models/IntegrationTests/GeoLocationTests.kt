package ca.ualberta.taskr.models.IntegrationTests

import android.content.Intent
import android.widget.Button
import ca.ualberta.taskr.AddLocationToTaskActivity
import ca.ualberta.taskr.EditTaskActivity
import ca.ualberta.taskr.R
import com.mapbox.mapboxsdk.maps.MapView
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

/**
 * Created by marissasnihur on 2018-03-25.
 *
 */

@RunWith(RobolectricTestRunner::class)
class GeoLocationTests {

    private lateinit var activity: AddLocationToTaskActivity
    @Test
    fun TestGeoLocationTest() {

    }
    @Before
    fun setUp(){
        activity = Robolectric.setupActivity(AddLocationToTaskActivity::class.java)
        //.create()
        //.resume()
        //.get()

    }

    @Test
    fun checkActivityNotNull() {
        Assert.assertNotNull(activity)
    }

    @Test
    fun testOnMapClick(){
        //val point = LatLng(54.56,-113.56)

        val mapView: MapView = activity.findViewById(R.id.rangeMapView)

        mapView.performClick()

    }

    @Test
    fun testOnButtonClick(){
        val button: Button = activity.findViewById(R.id.add_location)
        button.performClick()

        val intent: Intent = Shadows.shadowOf(activity).peekNextStartedActivity()

        Assert.assertEquals(EditTaskActivity::class.java.canonicalName, intent.component.className)


    }

}
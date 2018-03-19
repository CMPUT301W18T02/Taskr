package ca.ualberta.taskr.models.UserCases

import android.content.Intent
import android.widget.Button
import ca.ualberta.taskr.AddLocationToTaskActivity
import ca.ualberta.taskr.BuildConfig
import ca.ualberta.taskr.EditTaskActivity
import ca.ualberta.taskr.R
import ca.ualberta.taskr.models.Bid
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import kotlinx.android.synthetic.main.activity_add_location_to_task.view.*
import org.assertj.core.internal.cglib.core.TypeUtils.getClassName
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.Shadows
import org.robolectric.annotation.Config

/**
 * Created by marissasnihur on 2018-03-19.
 *
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class AddGeoLocationToTaskTest  {
    //Test for adding a task
    private lateinit var activity: AddLocationToTaskActivity


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

        assertEquals(EditTaskActivity::class.java.canonicalName,intent.component.className)


    }


}
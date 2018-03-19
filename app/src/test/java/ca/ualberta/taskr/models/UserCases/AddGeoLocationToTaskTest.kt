package ca.ualberta.taskr.models.UserCases

import ca.ualberta.taskr.AddLocationToTaskActivity
import ca.ualberta.taskr.models.Bid
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Created by marissasnihur on 2018-03-19.
 */

@RunWith(RobolectricTestRunner::class)
class AddGeoLocationToTaskTest  {
    //Test for adding a task
    val activity = AddLocationToTaskActivity::class

    @Test
    fun testOnMapClick(){
        val point = LatLng(54.56,-113.56)
        val mapboxMap: MapboxMap

    }


}
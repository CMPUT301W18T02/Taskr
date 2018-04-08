package ca.ualberta.taskr


import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import ca.ualberta.taskr.util.PermsUtil
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap

import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEnginePriority
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import android.content.Intent
import android.util.Log
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.elasticsearch.CachingRetrofit
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Displays a map with markers for all tasks within 5km of the current user's location. Popups
 * displaying task information are shown when markers are clicked, and tapping these popups
 * opens a [ViewTaskActivity] for that task.
 *
 * @author marissasnihur
 * @author xrendan
 * @property mapView [MapView] displaying map centered on user's location.
 * @property mapboxMap The map to be displayed.
 * @property locationEngine Provides location services.
 * @property masterTaskList List of all [Task] objects on server.
 * @property currentLocation Location of current user.
 * @see [ViewTaskActivity]
 */
class NearbyTasksActivity() : AppCompatActivity(), OnMapReadyCallback {


    @BindView(R.id.mapView)
    lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var locationEngine: LocationEngine
    private var masterTaskList: ArrayList<Task> = ArrayList()
    private var currentLocation: Location = Location("")

    /**
     * Initializes [MapBoxMap] and related views sets default values for
     * currentLocation in case user does not grant location permissions.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiYmFybmFidXN0aGViZW5pZ24iLCJhIjoiY2pldWI2MHN2NGhrZDJxbWU4dHdubmwxYSJ9.ZVq95tHTxTgyyppAfj3Jdw")
        setContentView(R.layout.activity_nearby_tasks)
        PermsUtil.getPermissions(this@NearbyTasksActivity)
        ButterKnife.bind(this)
        currentLocation.latitude = -7.942747
        currentLocation.longitude = -14.371925
        initializeLocationEngine()

        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)
    }


    /**
     * Default onStart() method with corresponding mapView method added.
     */
    public override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    /**
     * Default onResume() method with corresponding mapView method added.
     */
    public override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    /**
     * Default onPause() method with corresponding mapView method added.
     */
    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    /**
     * Default onStop() method with corresponding mapView method added.
     */
    public override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    /**
     * Default onLowMemory() method with corresponding mapView method added.
     */
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    /**
     * Default onDestroy() method with corresponding mapView method added.
     */
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    /**
     * Default onSaveInstanceState() method with corresponding mapView method added.
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState!!)
    }

    /**
     * Once [MapBoxMap] is initialized, populate masterTaskList and add tasks within 5km radius
     * of user to the map. Updates user's current location before setting the camera's position to
     * their location.
     * Sets a listener for each marker's info window so that, when clicked, a [ViewTaskActivity]
     * is started and provided with the task corresponding to that marker.
     *
     * @param mapboxMap
     * @see [MapBoxMap]
     * @see [CachingRetrofit]
     * @see [ViewTaskActivity]
     * @see [GenerateRetrofit]
     */
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        // Obtain all tasks from server, populate list, then add relevant tasks to map.
        CachingRetrofit(this).getTasks(object : ca.ualberta.taskr.models.elasticsearch.Callback<List<Task>> {
            override fun onResponse(response: List<Task>, responseFromCache: Boolean) {
                //TODO Offline signifier, handle responseFromCache
                masterTaskList.clear()
                masterTaskList.addAll(response)
                addTasksToMap()
            }
        }).execute()

        mapboxMap.setOnInfoWindowClickListener(
                fun(marker: Marker): Boolean {
                    for (task in masterTaskList) {
                        if (task.title == marker.title) {
                            val viewTaskIntent = Intent(applicationContext, ViewTaskActivity::class.java)
                            val bundle = Bundle()
                            val strTask = GenerateRetrofit.generateGson().toJson(task)
                            bundle.putString("TASK", strTask)
                            viewTaskIntent.putExtras(bundle)
                            startActivity(viewTaskIntent)

                        }
                    }
                    return true
                })
        updateCurrentLocation()
        setCameraPosition(currentLocation)
    }

    /**
     * If location permissions are enabled for the app, save the user's current location.
     */
    private fun updateCurrentLocation() {
        //TODO convert permissions to Permsutil
//        PermsUtil.getPermissions(this)
//        if (PermsUtil.checkPermission(this)) {
//
//            currentLocation = locationEngine.lastLocation
//
//        }
        if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PERMISSION_GRANTED) {
            currentLocation = locationEngine.lastLocation
        }
    }

    /**
     * Remove all existing markers from map, then add markers to map for each task within 5km of
     * user's current location.
     * Filters list of all task's for those whose locations are within 5km of user. For each
     * nearby task's marker, populate its popup fragment with the task's title and owner.
     *
     * @see [MapBoxMap]
     */
    private fun addTasksToMap() {
        mapboxMap.removeAnnotations()
        mapboxMap.addPolygon(generatePerimeter(latLngFromLocation(currentLocation), 5.0, 100))
        for (task in masterTaskList.filter { it.location != null && latLngFromLocation(currentLocation).distanceTo(it.location) <= 5000 }) {
            if (task.location != null) {
                mapboxMap.addMarker(MarkerOptions()
                        .position(task.location)
                        .title(task.title)
                        .snippet("owner: " + task.owner))
            }


        }
    }


/*override fun onMarkerClick(marker: Marker): Boolean {
    return true
}*/


//    override fun onMapClick(point: LatLng) {
//        if (position == null) {
//            marker = mapboxMap.addMarker(MarkerOptions()
//                    .position(point))
//            position = point
//        } else {
//            marker.remove()
//            marker = mapboxMap.addMarker(MarkerOptions()
//                    .position(point))
//            position = point
//
//        }
//    }

    /**
     * Initializes [LocationEngine] for providing location services.
     *
     * @see [LocationEngine]
     */
    private fun initializeLocationEngine() {
        locationEngine = Mapbox.getLocationEngine()
        locationEngine.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine.activate()
    }

    /**
     * Converts [Location] object to [LatLng].
     *
     * @param location The [Location] to be converted.
     * @return [LatLng] The converted location.
     */
    private fun latLngFromLocation(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    /**
     * Moves [MapBoxMap] camera to a provided location.
     *
     * @param location The camera's new location.
     * @see [MapBoxMap]
     */
    private fun setCameraPosition(location: Location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                latLngFromLocation(location), 10.0))
    }

    /**
     * Creates [PolygonOptions] for displaying a 5km perimeter around the user's current location.
     *
     * @param centerCoordinates Coordinates for the perimeter center.
     * @param radiusInKilometers
     * @param numberOfSides
     * @return [PolygonOptions]
     * @see [PolygonOptions]
     */
    private fun generatePerimeter(centerCoordinates: LatLng, radiusInKilometers: Double, numberOfSides: Int): PolygonOptions {
        val positions = ArrayList<LatLng>()
        val distanceX = radiusInKilometers / (111.319 * Math.cos(centerCoordinates.latitude * Math.PI / 180))
        val distanceY = radiusInKilometers / 110.574

        val slice = 2 * Math.PI / numberOfSides

        var theta: Double
        var x: Double
        var y: Double
        var position: LatLng
        for (i in 0 until numberOfSides) {
            theta = i * slice
            x = distanceX * Math.cos(theta)
            y = distanceY * Math.sin(theta)

            position = LatLng(centerCoordinates.latitude + y,
                    centerCoordinates.longitude + x)
            positions.add(position)
        }
        return PolygonOptions()
                .addAll(positions)
                .fillColor(Color.BLUE)
                .alpha(0.1f)
    }
}


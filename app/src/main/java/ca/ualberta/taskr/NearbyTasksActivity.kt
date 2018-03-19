package ca.ualberta.taskr

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import ca.ualberta.taskr.Perms.PermsUtil
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.location.LocationEnginePriority
import com.mapbox.services.android.telemetry.location.LocationEngineProvider
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEUTRAL
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.util.Log
import ca.ualberta.taskr.models.Task
import ca.ualberta.taskr.models.User
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit.Companion.generateRetrofit
import com.mapbox.mapboxsdk.location.LocationSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NearbyTasksActivity() : AppCompatActivity(), OnMapReadyCallback {

    @BindView(R.id.mapView)
    lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private var position: LatLng? = null
    private lateinit var marker: Marker
    private lateinit var locationPlugin: LocationLayerPlugin
    private lateinit var locationEngine: LocationEngine
    private var masterTaskList: ArrayList<Task> = ArrayList()
    private var currentLocation: Location = Location("")


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

    public override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    public override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState!!)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        GenerateRetrofit.generateRetrofit().getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                Log.i("network", response.body().toString())
                masterTaskList.clear()
                masterTaskList.addAll(response.body() as ArrayList<Task>)
                addTasksToMap()
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("network", "Network Failed!")
                t.printStackTrace()
            }
        })

        updateCurrentLocation()
        setCameraPosition(currentLocation)


    }

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

    private fun addTasksToMap() {
        mapboxMap.removeAnnotations()
        mapboxMap.addMarker(MarkerOptions()
                .position(latLngFromLocation(currentLocation))
                .title("Current Location"))
        mapboxMap.addPolygon(generatePerimeter(latLngFromLocation(currentLocation), 5.0, 100))


        for (task in masterTaskList.filter { it.location != null && latLngFromLocation(currentLocation).distanceTo(it.location) <= 5000 }) {
            if (task.location != null) {
                mapboxMap.addMarker(MarkerOptions()
                        .position(task.location)
                        .title(task.title)
                        .snippet("owner: " + task.owner))
                mapboxMap.setOnInfoWindowClickListener(
                            fun (marker: Marker): Boolean {
                                for (task1 in masterTaskList) {
                                    if (task1.title == marker.title) {
                                        var intent = Intent(this, ViewTaskActivity::class.java)
                                        var bundle = Bundle()
                                        var strTask = GenerateRetrofit.generateGson().toJson(task1)
                                        bundle.putString("DISPLAYTASK", strTask)
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    }
                                }
                                return true
                            })



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

    private fun initializeLocationEngine() {
        locationEngine = Mapbox.getLocationEngine()
        locationEngine.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine.activate()
    }

    private fun latLngFromLocation(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    private fun setCameraPosition(location: Location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                latLngFromLocation(location), 10.0))
    }

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


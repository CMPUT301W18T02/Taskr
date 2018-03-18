package ca.ualberta.taskr

import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.services.android.telemetry.location.LocationEngine
import com.mapbox.services.android.telemetry.permissions.PermissionsManager
import ca.ualberta.taskr.R.id.mapView
import com.mapbox.services.android.telemetry.location.LocationEngineListener
import com.mapbox.services.android.telemetry.permissions.PermissionsListener
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
//import com.mapbox.services.android.location.LostLocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import ca.ualberta.taskr.R.id.mapView
import com.mapbox.services.android.telemetry.location.LostLocationEngine
import android.widget.Toast
import android.support.annotation.NonNull
import ca.ualberta.taskr.Perms.PermsUtil


class NearbyTasksActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private lateinit var locationPlugin: LocationLayerPlugin
    private lateinit var locationEngine: LocationEngine
    private lateinit var mapboxMap: MapboxMap
    private lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiYmFybmFidXN0aGViZW5pZ24iLCJhIjoiY2pldWI2MHN2NGhrZDJxbWU4dHdubmwxYSJ9.ZVq95tHTxTgyyppAfj3Jdw")
        setContentView(R.layout.activity_nearby_tasks)
        PermsUtil.getPermissions(this@NearbyTasksActivity)
        mapView = findViewById<View>(R.id.mapView) as MapView
        println("HERE")
        mapView!!.onCreate(savedInstanceState)
        //TODO: implement range & user location services
        /*mapView!!.getMapAsync{ mapboxMap ->
            this@NearbyTasksActivity.mapboxMap = mapboxMap
            enableLocationPlugin()

        }


    }

    private fun enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermsUtil.checkPermission(this@NearbyTasksActivity)) {
            // Create an instance of LOST location engine
            initializeLocationEngine()

            locationPlugin = LocationLayerPlugin(mapView!!, mapboxMap, locationEngine)
            //PermsUtil.checkPermission(this@NearbyTasksActivity)
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING)
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    private fun initializeLocationEngine() {
        locationEngine = LostLocationEngine(this@NearbyTasksActivity)
        locationEngine.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine.activate()
        PermsUtil.checkPermission(this@NearbyTasksActivity)
        val lastLocation = locationEngine.lastLocation
        if (lastLocation != null) {
            setCameraPosition(lastLocation)
        } else {
            locationEngine.addLocationEngineListener(this)
        }
    }

    private fun setCameraPosition(location: Location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(location.getLatitude(), location.getLongitude()), 16.0))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {

    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationPlugin()
        } else {
            Toast.makeText(this, "You didn't grant location permissions.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onConnected() {
        PermsUtil.checkPermission(this@NearbyTasksActivity)
        locationEngine.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            setCameraPosition(location)
            locationEngine.removeLocationEngineListener(this)
        }
    }*/
    }
    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState!!)
    }
}

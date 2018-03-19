package ca.ualberta.taskr

//import com.mapbox.services.android.location.LostLocationEngine;
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


class NearbyTasksActivity() : AppCompatActivity(), OnMapReadyCallback, MapboxMap.OnMapClickListener {
      //, LocationEngineListener, OnMapReadyCallback, MapboxMap.OnMapClickListener

    /*@BindView(R.id.mapView)
    lateinit var mapView: MapView

    private lateinit var locationPlugin: LocationLayerPlugin
    private lateinit var locationEngine: LocationEngine
    private lateinit var mapboxMap: MapboxMap*/
/*
    override fun onConnected() {
        PermsUtil.checkPermission(this@NearbyTasksActivity)
        locationEngine.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            setCameraPosition(location)
            locationEngine.removeLocationEngineListener(this)
        }
    }




    private fun enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermsUtil.checkPermission(this@NearbyTasksActivity)) {
            // Create an instance of LOST location engine
            initializeLocationEngine()

            locationPlugin = LocationLayerPlugin(mapView!!, mapboxMap!!, locationEngine)
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING)
        } else {
            PermsUtil.getPermissions(this@NearbyTasksActivity)
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
            //locationEngine.addLocationEngineListener(this)
        }
    }

    private fun setCameraPosition(location: Location) {
        mapboxMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(location.getLatitude(), location.getLongitude()), 16.0))
    }*/

    @BindView(R.id.mapView)
    lateinit var mapView: MapView
    private var mapboxMap: MapboxMap? = null
    private var position: LatLng? = null
    private lateinit var marker: Marker
    private lateinit var locationPlugin: LocationLayerPlugin
    private lateinit var locationEngine: LocationEngine



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiYmFybmFidXN0aGViZW5pZ24iLCJhIjoiY2pldWI2MHN2NGhrZDJxbWU4dHdubmwxYSJ9.ZVq95tHTxTgyyppAfj3Jdw")
        setContentView(R.layout.activity_nearby_tasks)
        PermsUtil.getPermissions(this@NearbyTasksActivity)
        ButterKnife.bind(this)
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
        mapboxMap.addOnMapClickListener(this)

    }

    /*override fun onMarkerClick(marker: Marker): Boolean {
        return true
    }*/


    override fun onMapClick(point: LatLng) {
        if (position == null) {
            marker = mapboxMap!!.addMarker(MarkerOptions()
                    .position(point))
            position = point
        } else {
            marker.remove()
            marker = mapboxMap!!.addMarker(MarkerOptions()
                    .position(point))
            position = point

        }
    }
}

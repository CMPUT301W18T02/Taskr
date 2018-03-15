package ca.ualberta.taskr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ca.ualberta.taskr.R.id.mapView
import com.mapbox.mapboxsdk.Mapbox

import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng


class AddLocationToTaskActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var destMarker: Marker
    private lateinit var currentCoord: LatLng
    private lateinit var destCoord: LatLng


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.access_token))
        setContentView(R.layout.activity_add_location_to_task)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.setStyleUrl(Style.MAPBOX_STREETS)

        fun onMapReady(map: MapboxMap){
            map.addOnMapClickListener { MapboxMap.OnMapClickListener { point: LatLng ->
                destCoord = point
                destMarker = map.addMarker(MarkerOptions().position(destCoord))
                }
            } }
        }




    override fun onStart(){
        super.onStart()
        mapView.onStart()
    }

    override fun onResume(){
        super.onResume()
        mapView.onResume()
    }

    override fun onPause(){
        super.onPause()
        mapView.onPause()
    }

    override fun onStop(){
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }



}

package ca.ualberta.taskr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import ca.ualberta.taskr.R.id.mapView
import com.mapbox.mapboxsdk.Mapbox

import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback


class AddLocationToTaskActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private lateinit var button: Button
    private lateinit var point: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiYmFybmFidXN0aGViZW5pZ24iLCJhIjoiY2pldWI2MHN2NGhrZDJxbWU4dHdubmwxYSJ9.ZVq95tHTxTgyyppAfj3Jdw")
        setContentView(R.layout.activity_add_location_to_task)
        mapView = findViewById<View>(R.id.rangeMapView) as MapView
        //button = findViewById(R.id.add_location)
        println("HERE")
        mapView!!.onCreate(savedInstanceState)

        //TODO implement onMapClickListener - then done.

        mapView!!.getMapAsync(OnMapReadyCallback { mapboxMap ->
            mapboxMap.addMarker(MarkerOptions()
                    .position(LatLng(52.4631, -113.7286))
                    .title("Location")
                    .snippet("YOU ARE HERE"))
        })

        /*button.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View): Unit {
                val intent = Intent(applicationContext, AddLocationToTaskActivity::class.java)
                intent.putExtra("LatLng", point)
                //TODO send intent back [with coords] .
            }
        })*/


    }

    public override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    public override fun onStop() {
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

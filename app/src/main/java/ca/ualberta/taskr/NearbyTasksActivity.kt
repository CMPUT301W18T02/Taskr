package ca.ualberta.taskr

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ca.ualberta.taskr.Perms.PermsUtil
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.style.sources.VectorSource;
import ca.ualberta.taskr.R.id.mapView
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback


class NearbyTasksActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var vectorSource: VectorSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermsUtil.getPermissions(this@NearbyTasksActivity)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_nearby_tasks)
        mapView = findViewById<MapView>(R.id.rangeMapView) as MapView
        mapView.onCreate(savedInstanceState)
        //mapView.setStyleUrl(Style.DARK)

       /* mapView.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(mapboxMap: MapboxMap) {
                mapboxMap.setStyleUrl(Style.DARK)
            }
        })*/


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

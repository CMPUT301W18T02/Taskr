package ca.ualberta.taskr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * Class adds a Location to task, if no Location is specified returns a null position back to
 * the EditTask activity.
 *
 * If Location is specified, returns position (LatLng) when add_location button is pressed.
 *
 * //TODO: Upon clicking on the marker - marker should delete itself.
 * //TODO: Change marker color from red to purple hue - stretch goal.
 */
class AddLocationToTaskActivity : AppCompatActivity(), OnMapReadyCallback, MapboxMap.OnMapClickListener, MapboxMap.OnMarkerClickListener {
    @BindView(R.id.rangeMapView)
    lateinit var mapView: MapView
    private var mapboxMap: MapboxMap? = null
    @BindView(R.id.add_location)
    lateinit var button: Button
    private var position: LatLng? = null
    private lateinit var marker: Marker


    @OnClick(R.id.add_location)
    fun sendBack() {
        val intent = Intent(applicationContext, EditTaskActivity::class.java)
        intent.putExtra("LatLng", position)
        startActivity(intent)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiYmFybmFidXN0aGViZW5pZ24iLCJhIjoiY2pldWI2MHN2NGhrZDJxbWU4dHdubmwxYSJ9.ZVq95tHTxTgyyppAfj3Jdw")
        setContentView(R.layout.activity_add_location_to_task)
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

    override fun onMarkerClick(marker: Marker): Boolean {
        return true
    }


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

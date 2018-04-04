package ca.ualberta.taskr

import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import ca.ualberta.taskr.R.id.mapView
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory

import com.mapbox.mapboxsdk.constants.Style


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
    private lateinit var mapboxMap: MapboxMap
    @BindView(R.id.add_location)
    lateinit var button: Button
    private var position: LatLng? = null
    private lateinit var marker: Marker
    private lateinit var iconFactory: IconFactory
    private lateinit var iconDrawable: Drawable
    private lateinit var icon: Icon


    @OnClick(R.id.add_location)
    fun sendBack() {
        val intent = Intent()
        intent.putExtra("position", GenerateRetrofit.generateGson().toJson(position))
        setResult(RESULT_OK, intent)
        finish()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_add_location_to_task)
        ButterKnife.bind(this)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        position = GenerateRetrofit.generateGson().fromJson(intent.getStringExtra("position"), LatLng::class.java)
        println(position)

    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
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
        if (position != null) {
            setCameraPosition(position!!)

            marker = mapboxMap.addMarker(MarkerOptions()
                    .position(position))
        }
        mapboxMap.addOnMapClickListener(this)

    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return true
    }

    /**
     * On Map Click - places a marker on the map, if a marker is currently placed
     * on the map - removes it and places a marker in the new position.
     */


    override fun onMapClick(point: LatLng) {
        /*iconFactory = IconFactory.getInstance(this)
        iconDrawable = ContextCompat.getDrawable(this, R.drawable.purple_marker)
        icon = iconFactory.fromDrawable(iconDrawable)*/
        println(point)
        if (position == null) {
            marker = mapboxMap.addMarker(MarkerOptions()
                    .position(point))
            position = point
        } else {
            marker.remove()
            marker = mapboxMap.addMarker(MarkerOptions()
                    .position(point))
            position = point

        }
    }

    private fun latLngFromLocation(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    private fun setCameraPosition(point: LatLng) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                point, 10.0))
    }


}

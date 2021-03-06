package ca.ualberta.taskr

import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.models.elasticsearch.GenerateRetrofit
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback


/**
 * Class adds a Location to task, if no Location is specified returns a null position back to
 * the EditTask activity.
 *
 * If Location is specified, returns position (LatLng) when add_location button is pressed.
 *
 * @author marissasnihur
 * @author xrendan
 * @property mapView
 * @property button Button for confirming selected location.
 * @property position Coordinates of marker on map.
 * //TODO: Upon clicking on the marker - marker should delete itself.
 * //TODO: Change marker color from red to purple hue - stretch goal.
 */
class AddLocationToTaskActivity : AppCompatActivity(), OnMapReadyCallback, MapboxMap.OnMapClickListener, MapboxMap.OnMarkerClickListener {
    @BindView(R.id.rangeMapView)
    lateinit var mapView: MapView

    @BindView(R.id.addLocationToolbar)
    lateinit var toolbar: Toolbar

    private lateinit var mapboxMap: MapboxMap
    @BindView(R.id.add_location)
    lateinit var button: Button
    private var position: LatLng? = null
    private lateinit var marker: Marker
    private lateinit var iconFactory: IconFactory
    private lateinit var iconDrawable: Drawable
    private lateinit var icon: Icon


    /**
     * Returns the selected location.
     *
     * @see [GenerateRetrofit]
     */
    @OnClick(R.id.add_location)
    fun sendBack() {
        val intent = Intent()
        intent.putExtra("position", GenerateRetrofit.generateGson().toJson(position))
        setResult(RESULT_OK, intent)
        finish()

    }

    /**
     * Initializes map view and obtains marker location from a serialized [LatLng] object.
     *
     * @param savedInstanceState
     * @see [GenerateRetrofit]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_add_location_to_task)
        ButterKnife.bind(this)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        position = GenerateRetrofit.generateGson().fromJson(intent.getStringExtra("position"), LatLng::class.java)

        // Initialize toolbar for back button.
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

    }


    /**
     * Default onStart method with mapView added.
     */
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    /**
     * Default onResume method with mapView added.
     */
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    /**
     * Default onPause method with mapView added.
     */
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    /**
     * Default onStop method with mapView added.
     */
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    /**
     * Default onLowMemory method with mapView added.
     */
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    /**
     * Default onDestroy method with mapView added.
     */
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()

    }

    /**
     * Default onSaveInstanceState method with mapView added.
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState!!)
    }

    /**
     * Initializes click listener for map. If position object was provided
     * on activity start, moves camera to location and sets marker.
     *
     * @param mapboxMap
     * @see [MapBoxMap]
     */
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        if (position != null) {
            setCameraPosition(position!!)

            marker = mapboxMap.addMarker(MarkerOptions()
                    .position(position))
        }
        mapboxMap.addOnMapClickListener(this)

    }

    /**
     * When marker is clicked, it is removed from the mapview added.
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        return true
    }

    /**
     * Places a marker on the map. If a marker is currently placed on the map,
     * removes it and places a marker in the new position.
     *
     * @param point
     * @see [MapBoxMap]
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

    /**
     * Moves camera to a given point on the map.
     *
     * @param point
     * @see [MapboxMap]
     */
    private fun setCameraPosition(point: LatLng) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                point, 10.0))
    }

    /**
     * Process button presses from the tool bar.
     *
     * @param item
     * @return [Boolean]
     * @see [Toolbar]
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

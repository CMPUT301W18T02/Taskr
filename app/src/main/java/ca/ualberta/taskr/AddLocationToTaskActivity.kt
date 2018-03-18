package ca.ualberta.taskr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mapbox.mapboxsdk.Mapbox

import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback


class AddLocationToTaskActivity : AppCompatActivity(), OnMapReadyCallback, MapboxMap.OnMapClickListener {
    @BindView(R.id.rangeMapView)
    lateinit var mapView: MapView
    private var mapboxMap: MapboxMap? = null
    @BindView(R.id.add_location)
    lateinit var button: Button
    private lateinit var position: LatLng


    @OnClick(R.id.add_location)
    fun sendBack(){
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

        //TODO implement onMapClickListener - then done.

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

    override fun onMapClick(point: LatLng) {
        mapboxMap!!.addMarker(MarkerOptions()
                .position(point)
                .title("Location")
                .snippet("YOU ARE HERE"))
        position = point
    }

}

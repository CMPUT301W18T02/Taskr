package ca.ualberta.taskr

//import android.support.v7.app.AppCompatActivity
//import android.os.Bundle
//import android.view.View
//import butterknife.BindView
//import butterknife.ButterKnife
//
//import com.mapbox.mapboxsdk.Mapbox
//import com.mapbox.mapboxsdk.constants.Style
//import com.mapbox.mapboxsdk.maps.MapView
//import com.mapbox.mapboxsdk.maps.MapboxMap
//import com.mapbox.mapboxsdk.maps.OnMapReadyCallback

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



//class NearbyTasksActivity : AppCompatActivity() {
//
////    @BindView(R.id.rangemapView)
////    lateinit var mapView: MapView
//private var mapView: MapView? = null
//
//
//    public override fun onStart() {
//        super.onStart()
//        mapView!!.onStart()
//    }
//
//    public override fun onResume() {
//        super.onResume()
//        mapView!!.onResume()
//    }
//
//    public override fun onPause() {
//        super.onPause()
//        mapView!!.onPause()
//    }
//
//    public override fun onStop() {
//        super.onStop()
//        mapView!!.onStop()
//    }
//
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView!!.onLowMemory()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mapView!!.onDestroy()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle?) {
//        super.onSaveInstanceState(outState)
//        mapView!!.onSaveInstanceState(outState!!)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        Mapbox.getInstance(this, getString(R.string.access_token))
////        setContentView(R.layout.activity_nearby_tasks)
////        ButterKnife.bind(this)
////
////        mapView.onCreate(savedInstanceState)
////        mapView.setStyleUrl(getString(R.string.style_url))
//
//        //TODO - track location of user && include the 5KM range.
//        super.onCreate(savedInstanceState)
//        Mapbox.getInstance(this, "pk.eyJ1IjoiYmFybmFidXN0aGViZW5pZ24iLCJhIjoiY2pldWI2MHN2NGhrZDJxbWU4dHdubmwxYSJ9.ZVq95tHTxTgyyppAfj3Jdw")
//        setContentView(R.layout.activity_nearby_tasks)
//        mapView = findViewById<View>(R.id.rangemapView) as MapView
//        mapView!!.onCreate(savedInstanceState)
//
//    }
//}

class NearbyTasksActivity : AppCompatActivity() {
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoiYmFybmFidXN0aGViZW5pZ24iLCJhIjoiY2pldWI2MHN2NGhrZDJxbWU4dHdubmwxYSJ9.ZVq95tHTxTgyyppAfj3Jdw")
        setContentView(R.layout.activity_nearby_tasks)
        mapView = findViewById<View>(R.id.mapView) as MapView
        println("HERE")
        mapView!!.onCreate(savedInstanceState)

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

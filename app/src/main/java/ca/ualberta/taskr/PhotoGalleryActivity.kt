package ca.ualberta.taskr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import ca.ualberta.taskr.adapters.AddPhotosListAdapter

class PhotoGalleryActivity : AppCompatActivity() {

    @BindView(R.id.photosToolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.photosList)
    lateinit var photoList: RecyclerView

    lateinit var photoListAdapter: AddPhotosListAdapter

    private lateinit var viewManager: RecyclerView.LayoutManager

    var currentPhotosList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)
        ButterKnife.bind(this)


        setSupportActionBar(toolbar)
        var actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        currentPhotosList.addAll(intent.getStringArrayListExtra("currentPhotos"))

        photoListAdapter = AddPhotosListAdapter(currentPhotosList)

        // Build up recycle view
        viewManager = LinearLayoutManager(this)
        photoList.apply {
            layoutManager = viewManager
            adapter = photoListAdapter
        }

        // Set empty click listener
        photoListAdapter.setClickListener(View.OnClickListener {})
    }

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

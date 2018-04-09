package ca.ualberta.taskr

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import ca.ualberta.taskr.adapters.PhotosListAdapter

/**
 * PhotoGalleryActivity
 *
 * This class allows for the ability to view photos for a given task
 *
 * @author eyesniper2
 * @see AppCompatActivity
 */
class PhotoGalleryActivity : AppCompatActivity() {

    @BindView(R.id.photosToolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.photosList)
    lateinit var photoList: RecyclerView

    private lateinit var photoListAdapter: PhotosListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var currentPhotosList: ArrayList<String> = ArrayList()

    /**
     * Initializes the view and obtains photos from an [Intent] object.
     *
     * @param savedInstanceState
     * @see [Intent]
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)
        ButterKnife.bind(this)


        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        currentPhotosList.addAll(intent.getStringArrayListExtra("currentPhotos"))

        photoListAdapter = PhotosListAdapter(currentPhotosList)

        // Build up recycle view
        viewManager = LinearLayoutManager(this)
        photoList.apply {
            layoutManager = viewManager
            adapter = photoListAdapter
        }

        // Set empty click listener
        photoListAdapter.setClickListener(View.OnClickListener {})
    }

    /**
     * Handle when back button in [toolbar] is pressed
     *
     * @param item The menu item pressed
     * @return result
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

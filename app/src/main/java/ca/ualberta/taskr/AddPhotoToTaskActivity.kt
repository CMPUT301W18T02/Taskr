package ca.ualberta.taskr

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.adapters.PhotosListAdapter
import ca.ualberta.taskr.util.PhotoConversion


/**
 * AddPhotoToTaskActivity
 *
 * This class allows for the ability to add a photos to a given task.
 *
 * @author eyesniper2
 * @property REQUEST_IMAGE_CAPTURE RequestCode for opening camera app.
 * @property REQUEST_IMAGE_GALLERY RequestCode for opening image gallery.
 * @property photoList [RecyclerView] for currentPhotosList
 * @property toolbar [Toolbar] containing back button.
 * @property photoListAdapter [AddPhotosListAdapter] for rows of currentPhotosList
 * @property viewManager [RecyclerView.LayoutManager] for list adapter.
 * @property currentPhotosList List of task's photos
 * @see AppCompatActivity
 */
class AddPhotoToTaskActivity : AppCompatActivity() {

    private val requestImageCapture = 1
    private val requestImageGallery = 2

    @BindView(R.id.addPhotosList)
    lateinit var photoList: RecyclerView

    @BindView(R.id.addPhotosToolbar)
    lateinit var toolbar: Toolbar

    lateinit var photoListAdapter: PhotosListAdapter

    private lateinit var viewManager: RecyclerView.LayoutManager

    var currentPhotosList: ArrayList<String> = ArrayList()

    /**
     * Initializes the view and obtains previous photos from an [Intent] object.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo_to_task)
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

        photoListAdapter.setClickListener(View.OnClickListener {
            val position = photoList.getChildLayoutPosition(it)
            currentPhotosList.removeAt(position)
            photoListAdapter.notifyDataSetChanged()
        })
    }

    /**
     * Start up a photo capture activity to capture a picture for the users camera.
     *
     * @see MediaStore.ACTION_IMAGE_CAPTURE
     */
    @OnClick(R.id.takePhotoButton)
    fun onTakePhotoClicked() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, requestImageCapture)
        }
    }

    /**
     * Start up a gallery import activity to capture a picture for the users camera.
     *
     * @see Intent.ACTION_PICK
     * @see android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
     */
    @OnClick(R.id.selectImageFromGalleryButton)
    fun onTakeGalleryPhotoClicked() {
        val pickPhoto = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPhoto.resolveActivity(packageManager) != null) {
            startActivityForResult(pickPhoto, requestImageGallery)
        }
    }

    /**
     * Send photo strings back to the [EditTaskActivity]
     */
    @OnClick(R.id.addPhotosToTask)
    fun sendPhotosBack(){
        val intent = Intent()
        intent.putStringArrayListExtra("currentPhotos", currentPhotosList)
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * Handle when the back button in [toolbar] is pressed
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

    /**
     * Receives new photos and processes them.
     *
     * @param requestCode Request id
     * @param resultCode Result code
     * @param data Contains photo in [Bitmap] form.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data == null) return
        val thumbSize = 128
        if (requestCode == requestImageCapture && resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            val imageBitmap = extras.get("data") as Bitmap
            val thumbImage = ThumbnailUtils.extractThumbnail(imageBitmap,
                    thumbSize, thumbSize)
            currentPhotosList.add(PhotoConversion.getPhotoString(thumbImage))
            photoListAdapter.notifyDataSetChanged()
        }
        if (requestCode == requestImageGallery && resultCode == Activity.RESULT_OK) {
            val imageURI = data.data

            val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageURI)
            val thumbImage = ThumbnailUtils.extractThumbnail(imageBitmap,
                    thumbSize, thumbSize)
            currentPhotosList.add(PhotoConversion.getPhotoString(thumbImage))
            photoListAdapter.notifyDataSetChanged()
        }
    }
}

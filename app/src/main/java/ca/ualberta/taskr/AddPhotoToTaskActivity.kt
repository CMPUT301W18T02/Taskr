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
import ca.ualberta.taskr.adapters.AddPhotosListAdapter
import ca.ualberta.taskr.util.PhotoConversion


/**
 * AddPhotoToTaskActivity
 *
 * This class allows for the ability to add a photo to a given task
 *
 * @author eyesniper2
 * @property REQUEST_IMAGE_CAPTURE RequestCode for opening camera app.
 * @property REQUEST_IMAGE_GALLERY RequestCode for opening image gallery.
 * @property photoList [RecyclerView] for currentPhotosList
 * @property toolbar [Toolbar] containing back button.
 * @property photoListAdapter [AddPhotosListAdapter] for rows of currentPhotosList
 * @property viewManager [RecyclerView.LayoutManager] for list adapter.
 * @property currentPhotosList List of task's photos
 */
class AddPhotoToTaskActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2

    @BindView(R.id.addPhotosList)
    lateinit var photoList: RecyclerView

    @BindView(R.id.addPhotosToolbar)
    lateinit var toolbar: Toolbar

    lateinit var photoListAdapter: AddPhotosListAdapter

    private lateinit var viewManager: RecyclerView.LayoutManager

    var currentPhotosList: ArrayList<String> = ArrayList()

    /**
     * Initialize activity views, toolbar, and photo list adapter.
     *
     * @param savedInstanceState
     * @see [AddPhotosListAdapter]
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

        photoListAdapter = AddPhotosListAdapter(currentPhotosList)

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
     * On click of "Take Picture" button, start camera app.
     *
     * @see [MediaStore]
     */
    @OnClick(R.id.takePhotoButton)
    fun onTakePhotoClicked() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    /**
     * On click of "Gallery" button, start image gallery app.
     *
     * @see [MediaStore]
     */
    @OnClick(R.id.selectImageFromGalleryButton)
    fun onTakeGalleryPhotoClicked() {
        val pickPhoto = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPhoto.resolveActivity(packageManager) != null) {
            startActivityForResult(pickPhoto, REQUEST_IMAGE_GALLERY)
        }
    }

    /**
     * On click of "Add Photos" button, return list of added photos to [EditTaskActivity].
     *
     * @see [EditTaskActivity]
     */
    @OnClick(R.id.addPhotosToTask)
    fun sendPhotosBack(){
        val intent = Intent()
        intent.putStringArrayListExtra("currentPhotos", currentPhotosList)
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * Handles interactions with toolbar. If back button clicked, exit activity.
     *
     * @param item The clicked [MenuItem] in the toolbar
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

    /**
     * After user has added a photo via camera/gallery, this method converts the selected photo
     * to a string and saves it to currentPhotosList, updating the [RecyclerView] afterwards.
     *
     * @param requestCode Request code for activity which provided the new photo.
     * @param reseultCode
     * @param data Contains new photo
     * @see [AddPhotosListAdapter]
     * @see [PhotoConversion]
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data == null) return
        // If photo was taken with camera.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            val imageBitmap = extras.get("data") as Bitmap
            currentPhotosList.add(PhotoConversion.getPhotoString(imageBitmap))
            photoListAdapter.notifyDataSetChanged()
        }
        // If photo was provided from gallery.
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
//            val extras = data.extras
//            val imageBitmap = extras.get("data") as Bitmap

            val imageURI = data.data

            val THUMBSIZE = 64
            val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageURI)
            val thumbImage = ThumbnailUtils.extractThumbnail(imageBitmap,
                    THUMBSIZE, THUMBSIZE)
            currentPhotosList.add(PhotoConversion.getPhotoString(thumbImage))
            photoListAdapter.notifyDataSetChanged()
        }
    }
}

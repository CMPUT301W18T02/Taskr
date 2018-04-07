package ca.ualberta.taskr

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import ca.ualberta.taskr.adapters.AddPhotosListAdapter
import ca.ualberta.taskr.util.PhotoConversion
import java.io.IOException
import android.media.ThumbnailUtils




/**
 * AddPhotoToTaskActivity
 *
 * This class allows for the ability to add a photo to a given task
 */
class AddPhotoToTaskActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2

    @BindView(R.id.addPhotosList)
    lateinit var photoList: RecyclerView

    lateinit var photoListAdapter: AddPhotosListAdapter

    private lateinit var viewManager: RecyclerView.LayoutManager

    var currentPhotosList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo_to_task)
        ButterKnife.bind(this)

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


    @OnClick(R.id.takePhotoButton)
    fun onTakePhotoClicked() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    @OnClick(R.id.selectImageFromGalleryButton)
    fun onTakeGalleryPhotoClicked() {
        val pickPhoto = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPhoto.resolveActivity(packageManager) != null) {
            startActivityForResult(pickPhoto, REQUEST_IMAGE_GALLERY)
        }
    }

    @OnClick(R.id.addPhotosToTask)
    fun sendPhotosBack(){
        val intent = Intent()
        intent.putStringArrayListExtra("currentPhotos", currentPhotosList)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data == null) return
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            val imageBitmap = extras.get("data") as Bitmap
            currentPhotosList.add(PhotoConversion.getPhotoString(imageBitmap))
            photoListAdapter.notifyDataSetChanged()
        }
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

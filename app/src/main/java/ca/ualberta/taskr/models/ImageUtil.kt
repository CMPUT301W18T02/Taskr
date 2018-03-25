package ca.ualberta.taskr.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream

/**
 * Created by Michael Steer
 * on 2018-03-25.
 *
 * Created with the help of this stackoverflow post
 * https://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 */
class ImageUtil {
    fun bitmapToString(bitmap: Bitmap) : String{

        val stream = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        val out = Base64.encodeToString(bytes, Base64.DEFAULT)
        return out
    }

    fun stringToBitmap(string: String) : Bitmap {
        val bytes = Base64.decode(string, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        return bitmap
    }

    fun setImage(view: ImageView, bitmap : Bitmap) {
        view.setImageBitmap(bitmap)
    }
}
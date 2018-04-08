package ca.ualberta.taskr.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import ca.ualberta.taskr.exceptions.ImageTooLargeException
import java.io.ByteArrayOutputStream

// Referenced: https://www.thecrazyprogrammer.com/2016/10/android-convert-image-base64-string-base64-string-image.html

/**
 * PhotoConversion Class. This class is responsible for converting between image objects
 */
class PhotoConversion {
    companion object{

        private const val MaxImageByte:Int = 65536

        /**
         * Convert a [Bitmap] into a base 64 bit encoded string
         * @param bitmap the [Bitmap] to convert into a [String]
         * @return the base64 encoded [String]
         */
        fun getPhotoString(bitmap:Bitmap): String{
            val byteArray = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
            val imageBytes = byteArray.toByteArray()
            val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            if (imageString.toByteArray().size > MaxImageByte){
                throw ImageTooLargeException(imageString.toByteArray().size)
            }
            return Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }

        /**
         * Convert a base 64 encoded [String] into a [Bitmap]
         * @param bitmap the [Bitmap] to convert into a [String]
         * @return the [Bitmap]
         */
        fun getBitmapFromString(imageString:String): Bitmap{
            val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
    }
}
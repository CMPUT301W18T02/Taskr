package ca.ualberta.taskr.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import ca.ualberta.taskr.exceptions.ImageTooLargeException
import java.io.ByteArrayOutputStream

// Referenced: https://www.thecrazyprogrammer.com/2016/10/android-convert-image-base64-string-base64-string-image.html
class PhotoConversion {
    companion object{

        private const val MaxImageByte:Int = 65536

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

        fun getBitmapFromString(imageString:String): Bitmap{
            val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
    }
}
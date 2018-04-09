package ca.ualberta.taskr.util

import android.content.Context
import com.google.common.io.Files
import java.io.File
import java.util.*


/**
 * Created by xrend on 4/8/2018.
 */

class AdHelper {
    companion object {
        /**
         * Sets Ad free status for 10 minutes
         * @param context Context of the calling Activity
         */

        fun removeAdsTemporarily(context: Context, minutesWithoutAds: Long) {
            val TIME_AD_FREE = 1000 * 60 * minutesWithoutAds   // millis * seconds * number of minutes

            val file = File(context.filesDir, "adTime.json")
            if (!file.exists()) {
                file.createNewFile()
                Files.asCharSink(file, Charsets.UTF_8).write("0")
            }
            val expiryTimeInMillis: Long = Calendar.getInstance().timeInMillis + TIME_AD_FREE
            Files.asCharSink(file, Charsets.UTF_8).write(expiryTimeInMillis.toString())

        }

        /**
         * Returns True if User has ad free status
         * @param context Context of the calling Activity
         */

        fun isAdFree(context: Context): Boolean {

            val file = File(context.filesDir, "adTime.json")
            if (!file.exists()) {
                file.createNewFile()
                Files.asCharSink(file, Charsets.UTF_8).write("0")
            }
            return Calendar.getInstance().timeInMillis < Files.asCharSource(file, Charsets.UTF_8).read().toLong()

        }


    }
}
package ca.ualberta.taskr.models.elasticsearch

import android.util.Log
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

/**
 *  3/23/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

interface Callback<T> {
    fun onFailure() {

    }

    @Throws(IOException::class)
    fun onResponse(response: T, responseFromCache: Boolean)


}
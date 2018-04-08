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

/**
 * Callback interface for determining whether an asyncronous network operation was succesfull or not
 */
interface Callback<T> {

    /**
     * called when the thread producing the callback fails
     */
    fun onFailure() {

    }


    /**
     * called when the thread producing the callback succeeds and returns information
     * @param response the data returned
     * @param responseFromCache whether the data was returned by the cache or not
     */
    @Throws(IOException::class)
    fun onResponse(response: T, responseFromCache: Boolean)


}
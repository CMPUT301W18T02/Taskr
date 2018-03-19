package ca.ualberta.taskr.models.elasticsearch

import okhttp3.MediaType
import okhttp3.RequestBody

/**
 *  3/8/2018
 *
 *  Copyright (c) 2018 Brendan Samek. All Rights Reserved.
 */

/**
 * Query class.
 * Allows for the querying of lists of objects, returning the Elasticsearch ID of the object
 * in question in the event that it is found
 */
class Query {
    companion object {
        @JvmStatic
        fun taskQuery(owner: String, title: String, description: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"),"{\"_source\": false,\"query\": {\"bool\": {\"must\": [{\"match\" : {\"owner\": \"$owner\"}},{\"match\" : {\"title\": \"$title\"}},{\"match\" : {\"description\": \"$description\"}}]}}}")
        }

        @JvmStatic
        fun userQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"),"{\"_source\": false,\"query\": {\"bool\": {\"must\": [{\"match\" : {\"username\": \"$username\"}}]}}}")
        }

        @JvmStatic
        fun userOwnedTasksQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"),"{\"query\": {\"match\" : {\"owner\": \"$username\"}}}")
        }

        @JvmStatic
        fun userBiddedTasksQuery(username: String): RequestBody {
            return RequestBody.create(MediaType.parse("text/plain"),"{\"query\": {\"match\" : {\"bids.owner\": \"$username\"}}}")
        }


    }
}